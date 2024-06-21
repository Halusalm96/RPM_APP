package com.example.rpm_project;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;

import org.pytorch.IValue;
import org.pytorch.Module;
import org.pytorch.Tensor;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class CameraActivity extends AppCompatActivity {

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private PreviewView previewView;
    private Module rpmModule;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        previewView = findViewById(R.id.camera_preview);

        // rpm.pt 모델 로드
        rpmModule = Module.load(Utils.assetFilePath(this, "rpm.pt"));

        // 카메라 프로바이더 초기화
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        // 카메라 미리보기 시작
        startCamera();
    }

    private void startCamera() {
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

        imageAnalysis.setAnalyzer(Executors.newSingleThreadExecutor(), new ImageAnalysis.Analyzer() {
            @Override
            public void analyze(@NonNull ImageProxy imageProxy) {
                int rotationDegrees = imageProxy.getImageInfo().getRotationDegrees();
                Bitmap image = Utils.getBitmap(imageProxy);
                detectObjects(image, rotationDegrees);
                imageProxy.close();
            }
        });

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);
    }

    private void detectObjects(Bitmap image, int rotationDegrees) {
        // rpm.pt 모델을 사용한 객체 감지 로직 구현
        Tensor inputTensor = Utils.bitmapToTensor(image, rotationDegrees);
        Tensor outputTensor = rpmModule.forward(IValue.from(inputTensor)).toTensor();

        // 객체 감지 결과 처리
        float[] outputs = outputTensor.getDataAsFloatArray();
        // 결과를 사용하여 객체 박스, 클래스, 확률 등을 추출하는 로직 구현
        // ...

        // 예시: 감지된 객체의 클래스와 확률을 로그로 출력
        for (int i = 0; i < outputs.length; i += 6) {
            float classIndex = outputs[i + 5];
            float confidence = outputs[i + 4];
            if (confidence > 0.5) {
                Log.d("ObjectDetection", "Class: " + classIndex + ", Confidence: " + confidence);
            }
        }

        // 결과를 사용하여 UI에 표시하거나 다른 작업 수행
        // 예시: 감지된 객체 주위에 박스를 그리고 클래스와 확률을 표시
        // ...
    }
}
