package com.example.rpm_main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    private EditText edit_ID;
    private EditText edit_Password;
    private Button btn_Login;
    private TextView txt_Register;

    @Override
    protected void onCreate(Bundle savedInstanceState) { // 기본적으로 뷰를 초기화하고 이벤트 리스너를 설정합니다.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 뷰 초기화
        edit_ID = findViewById(R.id.edit_ID);
        edit_Password = findViewById(R.id.edit_Password);
        btn_Login = findViewById(R.id.btn_Login);
        txt_Register = findViewById(R.id.txt_Register);

        // 로그인 버튼 클릭 이벤트 설정
        btn_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = edit_ID.getText().toString();
                String password = edit_Password.getText().toString();

                if (!username.isEmpty() && !password.isEmpty()) {
                    // LoginTask 실행
                    new LoginTask(LoginActivity.this).execute(username, password);
                } else {
                    Toast.makeText(LoginActivity.this, "아이디와 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 회원가입 버튼 클릭 이벤트 설정
        txt_Register.setOnClickListener(new View.OnClickListener() { // 회원가입 버튼 설정을 여기로 이동하여 중복 제거
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}