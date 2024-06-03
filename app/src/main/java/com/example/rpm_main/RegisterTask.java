package com.example.rpm_main;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class RegisterTask extends AsyncTask<String, Void, Void> {
    @Override
    protected Void doInBackground(String... params) {
        String username = params[0];
        String password = params[1];

        try {
            URL url = new URL("http://192.168.123.118/register.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            // 데이터 전송
            String data = "username=" + username + "&password=" + password;
            byte[] postData = data.getBytes(StandardCharsets.UTF_8);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(postData);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // 성공적으로 요청을 보냄
            } else {
                // 요청 실패
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}