package com.example.rpm_project;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterPersonActivity extends AppCompatActivity {

    private EditText etName, etAge, etHeight;
    private Button btnInputCancel, btnInputComplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_person);

        etName = findViewById(R.id.et_name);
        etAge = findViewById(R.id.et_age);
        etHeight = findViewById(R.id.et_height);
        btnInputCancel = findViewById(R.id.btn_input_cancel);
        btnInputComplete = findViewById(R.id.btn_input_complete);

        // 입력 안하기 버튼 클릭 이벤트 처리
        btnInputCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 현재 액티비티 종료
                finish();
            }
        });

        // 입력 완료 버튼 클릭 이벤트 처리
        btnInputComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 입력된 정보 가져오기
                String name = etName.getText().toString().trim();
                String age = etAge.getText().toString().trim();
                String height = etHeight.getText().toString().trim();

                // 입력 값 유효성 검사 (이름, 나이, 키 모두 필수 입력)
                if (name.isEmpty() || age.isEmpty() || height.isEmpty()) {
                    Toast.makeText(RegisterPersonActivity.this, "모든 항목을 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    // 입력 완료 처리 (여기서 서버에 데이터 전송 등 필요한 작업 수행)
                    // 예시로 Toast 메시지 출력
                    Toast.makeText(RegisterPersonActivity.this, "이름: " + name + "\n나이: " + age + "\n키: " + height, Toast.LENGTH_SHORT).show();
                    // 필요한 경우 서버에 데이터 전송 등의 작업 추가
                }
            }
        });
    }
}
