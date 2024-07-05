package com.example.rpm_project;

import android.content.Intent;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_input);

        // Retrofit 인스턴스 생성
        apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        etCode = findViewById(R.id.et_code);
        btnSubmit = findViewById(R.id.btn_submit);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = etCode.getText().toString().trim();
                validateCode(code);
            }
        });
    }

    private void validateCode(String code) {
        // Retrofit을 사용하여 서버에 HTTP 요청 보내기
        Call<ResponseBody> call = apiService.validateCode(code);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String message = response.body().string();
                        // 응답에 따른 처리
                        if (message.equals("Welcome to the land!")) {
                            Toast.makeText(CodeInputActivity.this, "코드 검증 성공", Toast.LENGTH_SHORT).show();
                            // 인원정보 등록 페이지로 이동 또는 필요한 작업 수행
                            Intent intent = new Intent(CodeInputActivity.this, RegisterPersonActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(CodeInputActivity.this, "유효하지 않은 코드입니다.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(CodeInputActivity.this, "서버 응답 처리 중 오류 발생", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CodeInputActivity.this, "서버 응답 처리 중 오류 발생", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // 실패 처리
                Log.e(TAG, "Error: " + t.getMessage());
                Toast.makeText(CodeInputActivity.this, "서버 연결 중 오류 발생", Toast.LENGTH_SHORT).show();
            }
        });

        // 요청을 Volley 요청 큐에 추가
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        requestQueue.add(stringRequest);
    }
}
