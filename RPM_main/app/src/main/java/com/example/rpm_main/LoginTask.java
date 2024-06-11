package com.example.rpm_main;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class LoginTask extends AsyncTask<String, Void, Boolean> {

    private Context context;

    public LoginTask(Context context) {
        this.context = context;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        String userID = params[0];
        String userPassword = params[1];

        try {
            URL url = new URL("http://192.168.123.118/login.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            // 데이터 전송
            String data = "userID=" + userID + "&userPassword=" + userPassword;
            byte[] postData = data.getBytes(StandardCharsets.UTF_8);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(postData);
            }

            // 서버 응답 읽기
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            String responseString = response.toString();
            Log.d("LoginTask", "Server response: " + responseString);  // 응답을 로그에 출력

            // 서버 응답이 "Login successful"인 경우 true 반환
            return responseString.equals("Login successful");

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean success) {
        if (success) {
            Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
        } else {
            Toast.makeText(context, "Login failed", Toast.LENGTH_SHORT).show();
        }
    }
}