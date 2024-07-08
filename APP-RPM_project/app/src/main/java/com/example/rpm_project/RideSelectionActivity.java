package com.example.rpm_project;

import static com.example.rpm_project.QRScanActivity.SHARED_PREFS;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class RideSelectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_selection);

        // 기구 검색 버튼 클릭 이벤트 처리
        Button btnSearchRides = findViewById(R.id.btn_search_rides);
        btnSearchRides.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // CameraActivity로 전환
                Intent intent = new Intent(RideSelectionActivity.this, CameraActivity.class);
                startActivity(intent);
            }
        });
    }
    @Override
    public void onBackPressed() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        boolean isQrCodeRegistered = sharedPreferences.getBoolean("isQrCodeRegistered", false);

        if (isQrCodeRegistered) {
            // QR 코드가 등록된 경우, 이전 단계로 이동 (자녀 등록 화면으로 이동하는 예제)
            Intent intent = new Intent(this, RegisterPersonActivity.class);
            startActivity(intent);
            finish();
        } else {
            // QR 코드가 등록되지 않은 경우, QR 코드 인식 화면으로 이동
            Intent intent = new Intent(this, QRScanActivity.class);
            startActivity(intent);
            finish();
        }
    }
}