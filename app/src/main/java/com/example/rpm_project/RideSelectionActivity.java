package com.example.rpm_project;

import android.content.Intent;
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
}