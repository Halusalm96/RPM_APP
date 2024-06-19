package com.example.rpm_project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import androidx.appcompat.app.AppCompatActivity;

public class TicketActivity extends AppCompatActivity {
    private Button btnMember, btnTicket, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);

        btnMember = findViewById(R.id.btn_member);
        btnTicket = findViewById(R.id.btn_ticket);
        btnLogout = findViewById(R.id.btn_logout);

        // 회원 버튼 클릭 이벤트 처리
        btnMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });

        // 이용권 등록 버튼 클릭 이벤트 처리
        btnTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // QR 코드 스캔 화면으로 이동
                Intent intent = new Intent(TicketActivity.this, QRScanActivity.class);
                startActivityForResult(intent, REQUEST_QR_SCAN);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // SharedPreferences에서 사용자 정보 삭제
                SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear(); // 모든 데이터 삭제
                editor.apply();

                // MainActivity로 이동
                Intent intent = new Intent(TicketActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // 현재 TicketActivity 종료
            }
        });
    }

    private void showPopupMenu(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.popup_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_edit:
                        // 정보 수정 액티비티로 이동
                        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
                        int userNo = sharedPreferences.getInt("user_no", -1); // Default value -1 if not found

                        Intent editIntent = new Intent(TicketActivity.this, MemberActivity.class);
                        startActivity(editIntent);
                        return true;
                    case R.id.menu_logout:
                        // 로그아웃 처리
                        btnLogout.performClick(); // 로그아웃 버튼 클릭
                        return true;
                    default:
                        return false;
                }
            }
        });
        popup.show();
    }

    private static final int REQUEST_QR_SCAN = 1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_QR_SCAN) {
            if (resultCode == RESULT_OK) {
                // QR 코드 스캔 성공 시 처리할 로직 작성
                String qrCode = data.getStringExtra("qr_code");
                // 스캔된 QR 코드 값을 사용하여 필요한 작업 수행
            } else if (resultCode == RESULT_CANCELED) {
                // QR 코드 스캔 취소 시 처리할 로직 작성
                // 코드 입력 페이지로 이동
                Intent intent = new Intent(TicketActivity.this, CodeInputActivity.class);
                startActivity(intent);
            }
        }
    }
}
