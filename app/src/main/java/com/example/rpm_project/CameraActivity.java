package com.example.rpm_project;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;

import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CameraActivity extends AppCompatActivity {

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private PreviewView previewView;
    private Net net;
    private int modelInputWidth = 416; // 모델 입력 너비
    private int modelInputHeight = 416; // 모델 입력 높이
    private String[] classNames = {"롤러", "킹", "라운드", "휠"};
    private ExecutorService cameraExecutor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        previewView = findViewById(R.id.camera_preview);

        // OpenCV 초기화
        if (!OpenCVLoader.initDebug()) {
            Log.e("OpenCV", "OpenCV 초기화 실패");
            // OpenCV 초기화에 실패했을 때 추가적인 처리 가능
        } else {
            Log.d("OpenCV", "OpenCV 초기화 성공");
            System.loadLibrary("opencv_java4"); // OpenCV 네이티브 라이브러리 로드
        }

        // ONNX 모델 로드
        String modelPath = Utils.assetFilePath(this, "best.onnx");
        net = Dnn.readNetFromONNX(modelPath);

        // 카메라 미리보기 시작
        startCamera();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraExecutor != null) {
            cameraExecutor.shutdown();
        }
    }

    private void startCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void bindPreview(ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder().build();
        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        cameraExecutor = Executors.newSingleThreadExecutor();

        imageAnalysis.setAnalyzer(cameraExecutor, imageProxy -> {
            Bitmap bitmap = Utils.getBitmap(imageProxy);

            if (bitmap != null) {
                Bitmap resizedBitmap = null;
                try {
                    // 비트맵 크기 조정 (고정된 크기 사용)
                    resizedBitmap = Bitmap.createScaledBitmap(bitmap, modelInputWidth, modelInputHeight, true);

                    // Bitmap을 Mat 객체로 변환
                    Mat mat = new Mat(resizedBitmap.getHeight(), resizedBitmap.getWidth(), CvType.CV_8UC4);
                    org.opencv.android.Utils.bitmapToMat(resizedBitmap, mat);

                    // RGBA를 RGB로 변환
                    Mat rgbMat = new Mat();
                    Imgproc.cvtColor(mat, rgbMat, Imgproc.COLOR_RGBA2RGB);

                    // 이미지 전처리
                    Mat blob = Dnn.blobFromImage(rgbMat, 1 / 255.0, new Size(modelInputWidth, modelInputHeight), new Scalar(0, 0, 0), true, false);

                    // 추론 수행
                    net.setInput(blob);
                    List<Mat> result = new ArrayList<>();
                    List<String> outBlobNames = new ArrayList<>();
                    outBlobNames.add(0, net.getUnconnectedOutLayersNames().get(0));
                    net.forward(result, outBlobNames);

                    // 결과 후처리
                    processDetectionResult(result, bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    // ImageProxy 닫기
                    imageProxy.close();
                    // Bitmap 메모리 해제
                    if (bitmap != null && !bitmap.isRecycled()) {
                        bitmap.recycle();
                    }
                    if (resizedBitmap != null && !resizedBitmap.isRecycled()) {
                        resizedBitmap.recycle();
                    }
                }
            }
        });

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        try {
            cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);
        } catch (IllegalArgumentException e) {
            // 카메라를 찾을 수 없는 경우 처리
            Log.e("CameraActivity", "카메라를 찾을 수 없습니다.", e);
            // 사용자에게 알림 또는 다른 처리 수행
        }
    }

    private void processDetectionResult(List<Mat> result, Bitmap originalBitmap) {
        for (Mat mat : result) {
            for (int i = 0; i < mat.rows(); i++) {
                int classId = (int) mat.get(i, 1)[0];
                float confidence = (float) mat.get(i, 2)[0];
                float x = (float) mat.get(i, 3)[0];
                float y = (float) mat.get(i, 4)[0];
                float width = (float) mat.get(i, 5)[0];
                float height = (float) mat.get(i, 6)[0];

                if (confidence > 0.5) { // 신뢰도 임계값 설정
                    Log.d("Detection", "Detected object: " + classNames[classId] + " (Confidence: " + confidence + ")");
                    // 객체 감지 결과를 원본 이미지에 표시하는 함수 호출
                    drawDetectionResult(originalBitmap, classNames[classId], confidence, x, y, width, height);
                }
            }
            // Mat 객체 메모리 해제
            mat.release();
        }
    }

    private void drawDetectionResult(Bitmap originalBitmap, String className, float confidence, float x, float y, float width, float height) {
        // 결과 표시를 위해 별도 스레드에서 처리
        new Thread(() -> {
            // 원본 비트맵의 복사본 생성
            Bitmap resultBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);

            // 결과 표시를 위한 캔버스 생성
            Canvas canvas = new Canvas(resultBitmap);

            // 박스를 그리기 위한 Paint 설정
            Paint paint = new Paint();
            paint.setColor(Color.RED);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(2.0f);

            // 박스 그리기
            float left = x * resultBitmap.getWidth() - width * resultBitmap.getWidth() / 2;
            float top = y * resultBitmap.getHeight() - height * resultBitmap.getHeight() / 2;
            float right = left + width * resultBitmap.getWidth();
            float bottom = top + height * resultBitmap.getHeight();
            RectF rectF = new RectF(left, top, right, bottom);
            canvas.drawRect(rectF, paint);

            // 텍스트 그리기
            Paint textPaint = new Paint();
            textPaint.setColor(Color.RED);
            textPaint.setTextSize(32);

            float textX = left;
            float textY = bottom + 32; // 박스 아래쪽에 텍스트 표시
            canvas.drawText(className + " (Confidence: " + confidence + ")", textX, textY, textPaint);

            // UI 스레드에서 ImageView에 결과 비트맵 설정
            runOnUiThread(() -> {
                // 예시: imageView.setImageBitmap(resultBitmap);
                // 여기서는 PreviewView에 표시
//                previewView.setImageBitmap(resultBitmap);
            });
        }).start();
    }
}
