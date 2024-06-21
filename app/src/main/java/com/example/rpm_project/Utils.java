package com.example.rpm_project;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;

import androidx.camera.core.ImageProxy;

import org.pytorch.Tensor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class Utils {
    public static String assetFilePath(Context context, String assetName) {
        File file = new File(context.getFilesDir(), assetName);
        try (InputStream is = context.getAssets().open(assetName)) {
            try (OutputStream os = new FileOutputStream(file)) {
                byte[] buffer = new byte[4 * 1024];
                int read;
                while ((read = is.read(buffer)) != -1) {
                    os.write(buffer, 0, read);
                }
                os.flush();
            }
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ImageProxy에서 Bitmap으로 변환하는 메서드
    public static Bitmap getBitmap(ImageProxy imageProxy) {
        @SuppressLint("UnsafeOptInUsageError") Image image = imageProxy.getImage();
        if (image == null) {
            return null;
        }
        Image.Plane[] planes = image.getPlanes();
        ByteBuffer buffer = planes[0].getBuffer();
        int width = imageProxy.getWidth();
        int height = imageProxy.getHeight();
        int pixelStride = planes[0].getPixelStride();
        int rowStride = planes[0].getRowStride();
        int rowPadding = rowStride - pixelStride * width;
        Bitmap bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(buffer);
        image.close();
        return bitmap;
    }

    // Bitmap을 Tensor로 변환하는 메서드
    public static Tensor bitmapToTensor(Bitmap bitmap, int rotationDegrees) {
        // 여기에 Bitmap을 Tensor로 변환하는 코드를 추가하세요
        // 예시: PyTorch에서는 Bitmap을 Tensor로 변환하기 위해 이미지 전처리 과정이 필요합니다.
        return null;
    }

    // Tensor에서 float 배열로 데이터를 가져오는 메서드
    public static float[] getDataAsFloatArray(Tensor tensor) {
        // 여기에 Tensor에서 float 배열로 데이터를 가져오는 코드를 추가하세요
        // 예시: PyTorch에서는 Tensor에서 데이터를 추출하기 위한 코드가 필요합니다.
        return null;
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int rotationDegrees) {
        // Rotate the bitmap based on the rotation degrees
        return bitmap;
    }
}