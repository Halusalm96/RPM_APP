package com.example.rpm_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterPersonActivity extends AppCompatActivity {

    private LinearLayout childInfoContainer;
    private Button btnAddChildInfo, btnInputCancel, btnInputComplete;
    private int childCount = 1; // 자녀 정보 추가 버튼을 누를 때마다 증가하는 자녀 수
    private ApiService apiService;
    private SessionManager sessionManager;
    private int userNo; // 세션에서 가져온 사용자 식별 번호

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_person);

        childInfoContainer = findViewById(R.id.child_info_container);
        btnAddChildInfo = findViewById(R.id.btn_add_child_info);
        btnInputCancel = findViewById(R.id.btn_input_cancel);
        btnInputComplete = findViewById(R.id.btn_input_complete);

        sessionManager = SessionManager.getInstance(getApplicationContext());
        userNo = sessionManager.getUserNo(); // 세션 매니저를 통해 사용자 식별 번호 가져오기

        apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class); // Retrofit 인스턴스 가져오기

        // 자녀 정보 입력 추가 버튼 클릭 이벤트 처리
        btnAddChildInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addChildInfoFields();
            }
        });

        // 건너뛰기 버튼 클릭 이벤트 처리
        btnInputCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 놀이기구 선택 화면으로 이동
//                startActivity(new Intent(RegisterPersonActivity.this, RideSelectionActivity.class));

                // 웹뷰 액티비티로 이동
                startActivity(new Intent(RegisterPersonActivity.this, RideSelectionWebActivity.class));

                finish(); // 현재 액티비티 종료
            }
        });

        // 입력 완료 버튼 클릭 이벤트 처리
        btnInputComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitAllChildInfo();
            }
        });

        // 처음 시작할 때 기본 입력란 추가
        addChildInfoFields();
    }

    private void addChildInfoFields() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View childInfoView = inflater.inflate(R.layout.child_info_fields, childInfoContainer, false);

        // 자녀 정보 제목 설정
        TextView childTitle = childInfoView.findViewById(R.id.tv_child_title);
        childTitle.setText("자녀 " + childCount);

        // 부모 LinearLayout에 새로 생성된 자녀 정보 레이아웃 추가
        childInfoContainer.addView(childInfoView);

        // 자녀 수 증가
        childCount++;
    }

    private void submitAllChildInfo() {
        int childCount = childInfoContainer.getChildCount();
        boolean allFieldsFilled = true;

        for (int i = 0; i < childCount; i++) {
            View childInfoView = childInfoContainer.getChildAt(i);

            EditText etName = childInfoView.findViewById(R.id.et_name);
            EditText etAge = childInfoView.findViewById(R.id.et_age);
            EditText etHeight = childInfoView.findViewById(R.id.et_height);

            String name = etName.getText().toString().trim();
            String age = etAge.getText().toString().trim();
            String height = etHeight.getText().toString().trim();

            if (name.isEmpty() || age.isEmpty() || height.isEmpty()) {
                allFieldsFilled = false;
                break;
            }
        }

        if (!allFieldsFilled) {
            Toast.makeText(RegisterPersonActivity.this, "모든 항목을 입력해주세요.", Toast.LENGTH_SHORT).show();
        } else {
            // 자녀 정보를 서버에 전송하여 DB에 저장
            saveChildInfoToServer();
        }
    }

    private void saveChildInfoToServer() {
        int childCount = childInfoContainer.getChildCount();

        for (int i = 0; i < childCount; i++) {
            View childInfoView = childInfoContainer.getChildAt(i);

            EditText etName = childInfoView.findViewById(R.id.et_name);
            EditText etAge = childInfoView.findViewById(R.id.et_age);
            EditText etHeight = childInfoView.findViewById(R.id.et_height);

            String name = etName.getText().toString().trim();
            int age = Integer.parseInt(etAge.getText().toString().trim());
            int height = Integer.parseInt(etHeight.getText().toString().trim());

            ChildInfo childInfo = new ChildInfo(name, age, height);

            // Retrofit을 사용하여 서버에 자녀 정보 전송
            Call<Void> call = apiService.addChildInfo(userNo, childInfo);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(RegisterPersonActivity.this, "자녀 정보 저장 성공", Toast.LENGTH_SHORT).show();
                        // 놀이기구 선택 화면으로 이동
//                        startActivity(new Intent(RegisterPersonActivity.this, RideSelectionActivity.class));

                        // 웹뷰 액티비티로 이동
                        startActivity(new Intent(RegisterPersonActivity.this, RideSelectionWebActivity.class));
                        finish(); // 현재 액티비티 종료

                    } else {
                        Toast.makeText(RegisterPersonActivity.this, "자녀 정보 저장 실패", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(RegisterPersonActivity.this, "네트워크 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
