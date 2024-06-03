package com.example.rpm_main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button btnTicket, btnLogin, btnExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        btnTicket = findViewById(R.id.btn_ticket);
        btnLogin = findViewById(R.id.btn_login);
        btnExit = findViewById(R.id.btn_exit);

//        btnTicket.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // 이용권 등록 버튼 클릭 시 이용권 등록 화면으로 이동
//                Intent intent = new Intent(MainActivity.this, TicketActivity.class);
//                startActivity(intent);
//            }
//        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 로그인 버튼 클릭 시 로그인 화면으로 이동
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 종료 버튼 클릭 시 앱 종료
                finish();
            }
        });
    }
}