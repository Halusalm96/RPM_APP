package com.example.rpm_project;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.YuvImage;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;

import org.tensorflow.lite.Interpreter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CameraActivity extends AppCompatActivity {
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private PreviewView previewView;
    private int modelInputWidth = 640;
    private int modelInputHeight = 640;
    private int modelInputChannels = 3;
    private int outputHeight;
    private int outputWidth;
    private int numOutputs;
    private String[] classNames = {"rollercoaster", "viking", "merrygoround", "ferriswheel"};
    private ExecutorService cameraExecutor;
    private Interpreter tflite;
    private ObjectDetectionOverlay objectDetectionOverlay;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        previewView = findViewById(R.id.camera_preview);
        objectDetectionOverlay = findViewById(R.id.object_detection_overlay);

        // TFLite 모델 로드
        try {
            tflite = new Interpreter(loadModelFile("model2.tflite"), new Interpreter.Options().setNumThreads(4)); // 4 스레드 사용
            int[] outputShape = tflite.getOutputTensor(0).shape();
            outputHeight = outputShape[1];
            outputWidth = outputShape[2];
            numOutputs = outputShape[3];
            Log.d("TFLite", "TFLite 모델 로드 성공");
        } catch (IOException e) {
            Log.e("TFLite", "TFLite 모델 로드 실패", e);
            Toast.makeText(this, "TFLite 모델 로드 실패: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        // 카메라 미리보기 시작
        startCamera();
    }

    private MappedByteBuffer loadModelFile(String modelPath) throws IOException {
        AssetFileDescriptor fileDescriptor = this.getAssets().openFd(modelPath);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraExecutor != null) {
            cameraExecutor.shutdown();
        }
        if (tflite != null) {
            tflite.close();
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
            if (tflite == null) {
                Log.e("TFLite", "TFLite Interpreter is null");
                imageProxy.close();
                return;
            }

            Bitmap bitmap = Utils.getBitmap(imageProxy);

            if (bitmap != null) {
                try {
                    // 비트맵 크기 조정 (고정된 크기 사용)
                    Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, modelInputWidth, modelInputHeight, true);

                    // 이미지 전처리 및 추론
                    ByteBuffer inputBuffer = preProcessImage(resizedBitmap);
                    // TensorFlow Lite 모델 출력 텐서 형태에 맞게 Java 객체 생성
                    float[][][][] output = new float[1][outputHeight][outputWidth][numOutputs];
                    tflite.run(inputBuffer, output);

                    // 결과 후처리
                    processDetectionResult(output, bitmap);

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    // ImageProxy 닫기
                    imageProxy.close();
                    // Bitmap 메모리 해제
                    if (bitmap != null && !bitmap.isRecycled()) {
                        bitmap.recycle();
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
            Log.e("CameraActivity", "카메라를 찾을 수 없습니다.", e);
        }
    }

    private ByteBuffer preProcessImage(Bitmap bitmap) {
        int[] intValues = new int[modelInputWidth * modelInputHeight];
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        ByteBuffer imgData = ByteBuffer.allocateDirect(4 * modelInputWidth * modelInputHeight * modelInputChannels);
        imgData.order(ByteOrder.nativeOrder());

        for (int pixelValue : intValues) {
            imgData.putFloat(((pixelValue >> 16) & 0xFF) / 255.0f);
            imgData.putFloat(((pixelValue >> 8) & 0xFF) / 255.0f);
            imgData.putFloat((pixelValue & 0xFF) / 255.0f);
        }

        return imgData;
    }

    private void processDetectionResult(float[][][][] output, Bitmap originalBitmap) {
        List<ObjectDetectionOverlay.DetectionResult> results = new ArrayList<>();

        for (float[][] detectionGrid : output[0]) {
            for (float[] detection : detectionGrid) {
                // 신뢰도 점수 임계값 확인
                if (detection[4] > 8) { // 정확도 임계값을 0.6으로 설정
                    float x = detection[0];
                    float y = detection[1];
                    float width = detection[2];
                    float height = detection[3];
                    float confidence = detection[4];
                    int classId = (int) detection[5];

                    // classId가 classNames 배열의 범위 내에 있는지 확인
                    if (classId >= 0 && classId < classNames.length) {
                        String className = classNames[classId];

                        // 클래스 이름에 따른 매핑된 숫자를 로그에 출력
                        int mappedValue = mapClassNameToValue(className);
                        Log.d("Detection", "Class: " + className + ", Mapped Value: " + mappedValue);

                        // 바운딩 박스 좌표 계산
                        float left = (x - width / 2) * originalBitmap.getWidth() / modelInputWidth * 100;
                        float top = (y - height / 2) * originalBitmap.getHeight() / modelInputHeight * 200;
                        float right = (x + width / 2) * originalBitmap.getWidth() / modelInputWidth * 100;
                        float bottom = (y + height / 2) * originalBitmap.getHeight() / modelInputHeight * 200;

                        results.add(new ObjectDetectionOverlay.DetectionResult(className, confidence, left, top, right, bottom));
                    } else {
                        // 잘못된 클래스 ID 로그 생략
                    }
                }
            }
        }
        runOnUiThread(() -> objectDetectionOverlay.setDetectionResults(results));
    }

    private int mapClassNameToValue(String className) {
        switch (className) {
            case "rollercoaster":
                return 1;
            case "viking":
                return 4;
            case "merrygoround":
                return 2;
            case "ferriswheel":
                return 3;
            default:
                return -1; // 오류 또는 정의되지 않은 클래스
        }
    }

    private void saveBitmap(Bitmap bitmap, String filename) {
        File file = new File(getExternalFilesDir(null), filename);
        try (FileOutputStream out = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (IOException e) {
            Log.e("BitmapSave", "Failed to save bitmap", e);
        }
    }
}
