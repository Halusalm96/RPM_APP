package com.example.rpm_project;

// AndroidManifest.xml에서 인터넷 권한 추가 필요

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class CameraActivity extends AppCompatActivity {

    private static final String SERVER_URL = "http://192.168.123.119/upload.php"; // 서버의 PHP 파일 경로
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 사진 촬영 및 서버 전송
        takePhotoAndSendToServer();
    }

    private void takePhotoAndSendToServer() {
        // 여기서 사진을 촬영하여 Bitmap 형태로 가져옴 (생략)

        // Bitmap을 Base64 문자열로 변환
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        // 서버로 전송하는 AsyncTask 실행
        new UploadImageTask().execute(encodedImage);
    }

    private class UploadImageTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(SERVER_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                // 이미지 데이터 전송
                DataOutputStream outputStream = new DataOutputStream(conn.getOutputStream());
                String encodedImage = params[0];
                outputStream.writeBytes("encodedImage=" + encodedImage);
                outputStream.flush();
                outputStream.close();

                // 응답 확인
                InputStream inputStream = conn.getInputStream();
                // 여기서 서버 응답 처리 (생략)

                conn.disconnect();
                return "Upload success";
            } catch (Exception e) {
                Log.e(TAG, "Error: " + e.getMessage());
                return "Upload failed";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            // 업로드 결과 처리 (생략)
        }
    }
}
