package com.example.rpm_project;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.YuvImage;
import android.media.Image;
import android.util.Log;

import androidx.camera.core.ImageProxy;

import java.io.ByteArrayOutputStream;
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
            Log.e("Utils", "Failed to load asset file: " + assetName, e);
            return null;
        }
    }

    // ImageProxy에서 Bitmap으로 변환하는 메서드
    public static Bitmap getBitmap(ImageProxy imageProxy) {
        @SuppressLint("UnsafeOptInUsageError") Image image = imageProxy.getImage();
        if (image == null) {
            return null;
        }

        // Convert YUV to RGB
        ByteBuffer yBuffer = image.getPlanes()[0].getBuffer();
        ByteBuffer uBuffer = image.getPlanes()[1].getBuffer();
        ByteBuffer vBuffer = image.getPlanes()[2].getBuffer();

        int ySize = yBuffer.remaining();
        int uSize = uBuffer.remaining();
        int vSize = vBuffer.remaining();

        byte[] nv21 = new byte[ySize + uSize + vSize];

        yBuffer.get(nv21, 0, ySize);
        vBuffer.get(nv21, ySize, vSize);
        uBuffer.get(nv21, ySize + vSize, uSize);

        YuvImage yuvImage = new YuvImage(nv21, ImageFormat.NV21, image.getWidth(), image.getHeight(), null);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new android.graphics.Rect(0, 0, image.getWidth(), image.getHeight()), 100, out);
        byte[] jpegBytes = out.toByteArray();
        return android.graphics.BitmapFactory.decodeByteArray(jpegBytes, 0, jpegBytes.length);
    }

    public static float[][][][] bitmapToFloatArray(Bitmap bitmap, int modelInputWidth, int modelInputHeight, int rotationDegrees) {
        Bitmap resizedBitmap = resizeBitmap(bitmap, modelInputWidth, modelInputHeight);
        if (rotationDegrees != 0) {
            resizedBitmap = rotateBitmap(resizedBitmap, rotationDegrees);
        }

        float[][][][] floatValues = new float[1][3][modelInputHeight][modelInputWidth];

        int[] intValues = new int[modelInputWidth * modelInputHeight];
        resizedBitmap.getPixels(intValues, 0, resizedBitmap.getWidth(), 0, 0, resizedBitmap.getWidth(), resizedBitmap.getHeight());

        for (int y = 0; y < modelInputHeight; y++) {
            for (int x = 0; x < modelInputWidth; x++) {
                int pixelValue = intValues[y * modelInputWidth + x];
                // YOLO expects normalized values (0-1)
                floatValues[0][0][y][x] = ((pixelValue >> 16) & 0xFF) / 255.0f;  // R
                floatValues[0][1][y][x] = ((pixelValue >> 8) & 0xFF) / 255.0f;   // G
                floatValues[0][2][y][x] = (pixelValue & 0xFF) / 255.0f;          // B
            }
        }

        return floatValues;
    }

    private static Bitmap resizeBitmap(Bitmap bitmap, int modelInputWidth, int modelInputHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        // 이미지의 비율 유지를 위해 새로운 너비와 높이 계산
        float scaleWidth = ((float) modelInputWidth) / width;
        float scaleHeight = ((float) modelInputHeight) / height;

        // Matrix 객체 생성
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        // 이미지 회전 (옵션으로 회전 각도도 처리 가능)
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);

        return resizedBitmap;
    }

    // Bitmap을 Tensor로 변환하는 메서드 (현재 사용되지 않음)
    public static float[] bitmapToFloatArray(Bitmap bitmap, int rotationDegrees) {
        bitmap = rotateBitmap(bitmap, rotationDegrees);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float[] floatArray = new float[3 * width * height];

        int index = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = bitmap.getPixel(x, y);
                floatArray[index++] = ((pixel >> 16) & 0xFF) / 255.0f;  // Red
                floatArray[index++] = ((pixel >> 8) & 0xFF) / 255.0f;   // Green
                floatArray[index++] = (pixel & 0xFF) / 255.0f;          // Blue
            }
        }
        return floatArray;
    }

    // Bitmap을 회전하는 메서드
    public static Bitmap rotateBitmap(Bitmap bitmap, int rotationDegrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(rotationDegrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
    public static Bitmap decodeSampledBitmapFromResource(String path, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

}
