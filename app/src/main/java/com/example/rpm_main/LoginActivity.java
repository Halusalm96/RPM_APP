package com.example.rpm_main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;


public class LoginActivity extends AppCompatActivity {
    TextView sign;

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //회원가입 버튼
        sign = findViewById(R.id.signin);

        //회원가입 버튼 클릭시, 회원가입 페이지로 이동
        sign.setOnClickListener(v -> {
            Intent intent = new Intent(this, SignupActivity.class);
            startActivity(intent);
        });
    }
}