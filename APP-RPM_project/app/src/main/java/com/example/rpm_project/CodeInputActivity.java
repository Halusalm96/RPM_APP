package com.example.rpm_project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CodeInputActivity extends AppCompatActivity {

    private static final String TAG = CodeInputActivity.class.getSimpleName();
    private EditText etCode;
    private Button btnSubmit;
    private ApiService apiService;
    private SharedPreferences sharedPreferences;
    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String VERIFIED_CODE = "verifiedCode";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_input);

        apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        etCode = findViewById(R.id.et_code);
        btnSubmit = findViewById(R.id.btn_submit);

        sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = etCode.getText().toString().trim();
                validateCode(code);
            }
        });
    }

    private void validateCode(String code) {
        Call<ResponseBody> call = apiService.validateCode(code);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String message = response.body().string();
                        Log.d(TAG, "Server Response: " + message);
                        if (message.equals("Welcome to the land!")) {
                            Toast.makeText(CodeInputActivity.this, "코드 검증 성공", Toast.LENGTH_SHORT).show();

                            // 검증된 코드를 SharedPreferences에 저장
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(VERIFIED_CODE, code);
                            editor.putBoolean("isQrCodeRegistered", true); // QR 코드 등록 상태 저장
                            editor.apply();

                            Intent intent = new Intent(CodeInputActivity.this, RegisterPersonActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(CodeInputActivity.this, "유효하지 않은 코드입니다.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Error processing server response", e);
                        Toast.makeText(CodeInputActivity.this, "서버 응답 처리 중 오류 발생", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "Failed server response: " + response.code());
                    Toast.makeText(CodeInputActivity.this, "서버 응답 처리 중 오류 발생", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "Server connection error: " + t.getMessage());
                Toast.makeText(CodeInputActivity.this, "서버 연결 중 오류 발생", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onBackPressed() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        boolean isQrCodeRegistered = sharedPreferences.getBoolean("isQrCodeRegistered", false);

        if (isQrCodeRegistered) {
            // QR 코드가 등록된 경우, 기본 동작 수행
            super.onBackPressed();
        } else {
            // QR 코드가 등록되지 않은 경우, QR 코드 인식 화면으로 이동
            Intent intent = new Intent(this, QRScanActivity.class);
            startActivity(intent);
            finish();
        }
    }

}
