package com.example.rpm_project;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MemberActivity extends AppCompatActivity {

    private ApiService apiService;
    private TextView txtUserID, txtUserName, txtUserBirth, txtUserEmail, txtUserPhone;
    private Button btnEdit, btnDelete;
    private String userPassword;

    private static final String TAG = "MemberActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);

        apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        txtUserID = findViewById(R.id.myID);
        txtUserName = findViewById(R.id.myName);
        txtUserBirth = findViewById(R.id.myBirth);
        txtUserEmail = findViewById(R.id.myEmail);
        txtUserPhone = findViewById(R.id.myPhone);
        btnEdit = findViewById(R.id.editButton);
        btnDelete = findViewById(R.id.deleteButton); // 탈퇴 버튼 추가

        User user = (User) getIntent().getSerializableExtra("user_data");
        handleUserData(user);

        btnEdit.setOnClickListener(v -> showPasswordDialog(user));
        btnDelete.setOnClickListener(v -> showDeleteConfirmationDialog(user));

        TextView txtBack = findViewById(R.id.back);
        txtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToTicketActivity();
            }
        });
    }

    private void navigateToTicketActivity() {
        Intent intent = new Intent(MemberActivity.this, TicketActivity.class);
        startActivity(intent);
        finish(); // 현재 화면을 닫음
    }

    private void handleUserData(User user) {
        if (user != null) {
            String id = user.getUserId() != null ? user.getUserId() : "사용자 ID 없음";
            String name = user.getUserName() != null ? user.getUserName() : "사용자 이름 없음";
            String birth = user.getUserBirth() != null ? user.getUserBirth() : "생일 없음";
            String email = user.getUserEmail() != null ? user.getUserEmail() : "이메일 정보 없음";
            String phone = user.getUserNumber() != null ? user.getUserNumber() : "전화번호 정보 없음";

            txtUserID.setText(id);
            txtUserName.setText(name);
            txtUserBirth.setText(birth);
            txtUserEmail.setText(email);
            txtUserPhone.setText(phone);

            Log.d(TAG, "사용자 정보가 TextView에 설정되었습니다:");
            Log.d(TAG, "ID: " + id);
            Log.d(TAG, "Name: " + name);
            Log.d(TAG, "Birth: " + birth);
            Log.d(TAG, "Email: " + email);
            Log.d(TAG, "Phone: " + phone);
        } else {
            Log.d(TAG, "사용자 정보가 null입니다.");
        }
    }

    private void showPasswordDialog(User user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_password, null);
        EditText editPassword = dialogView.findViewById(R.id.editTextPassword);

        builder.setView(dialogView)
                .setTitle("비밀번호 확인")
                .setMessage("비밀번호를 입력하세요.")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String enteredPassword = editPassword.getText().toString();

                        // User 객체에서 저장된 비밀번호를 가져옵니다.
                        if (user != null) {
                            String savedPassword = user.getUserPw();

                            if (enteredPassword.equals(savedPassword)) {
                                Toast.makeText(MemberActivity.this, "비밀번호가 확인되었습니다. 정보를 수정하세요.", Toast.LENGTH_SHORT).show();
                                updateUserInfo(); // 정보 수정 메서드 호출
                            } else {
                                Toast.makeText(MemberActivity.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MemberActivity.this, "사용자 정보를 가져오지 못했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("취소", null)
                .create()
                .show();
    }

    private void updateUserInfo() {
        // SessionManager를 통해 사용자 정보 가져오기
        SessionManager sessionManager = new SessionManager(getApplicationContext());
        int userNo = sessionManager.getUserNo(); // 사용자 번호 가져오기

        if (userNo != -1) {
            User updatedUser = new User();
            updatedUser.setUserName(txtUserName.getText().toString());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                updatedUser.setUserBirth(String.valueOf(LocalDate.parse(txtUserBirth.getText().toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
            }
            updatedUser.setUserEmail(txtUserEmail.getText().toString());
            updatedUser.setUserNumber(txtUserPhone.getText().toString());

            Call<User> call = apiService.updateUser(userNo, updatedUser);
            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(MemberActivity.this, "정보가 성공적으로 수정되었습니다.", Toast.LENGTH_SHORT).show();
                        navigateToTicketActivity(); // 정보 수정 후 TicketActivity로 이동
                    } else {
                        try {
                            String errorResponse = response.errorBody().string();
                            Toast.makeText(MemberActivity.this, "정보 수정 실패: " + errorResponse, Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(MemberActivity.this, "정보 수정 실패", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Toast.makeText(MemberActivity.this, "네트워크 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "사용자 번호가 유효하지 않습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDeleteConfirmationDialog(User user) {
        // 계정 삭제 확인 다이얼로그
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_password, null);
        EditText editPassword = dialogView.findViewById(R.id.editTextPassword);

        builder.setView(dialogView)
                .setTitle("계정 삭제 확인")
                .setMessage("정말로 계정을 삭제하시겠습니까?\n삭제한 데이터는 복구할 수 없습니다.")
                .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 비밀번호 확인 다이얼로그에서 비밀번호 입력 후 처리
                        String enteredPassword = editPassword.getText().toString();

                        // User 객체에서 저장된 비밀번호를 가져옵니다.
                        if (user != null) {
                            String savedPassword = user.getUserPw();

                            if (enteredPassword.equals(savedPassword)) {
                                Toast.makeText(MemberActivity.this, "비밀번호가 확인되었습니다. 계정을 삭제합니다.", Toast.LENGTH_SHORT).show();
                                deleteUserInfo(); // 계정 삭제 메서드 호출
                            } else {
                                Toast.makeText(MemberActivity.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MemberActivity.this, "사용자 정보를 가져오지 못했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("취소", null)
                .create()
                .show();
    }


    private void deleteUserInfo() {
        // SessionManager를 통해 사용자 정보 가져오기
        SessionManager sessionManager = new SessionManager(getApplicationContext());
        int userNo = sessionManager.getUserNo(); // 사용자 번호 가져오기

        if (userNo != -1) {
            Call<Void> call = apiService.deleteUser(userNo);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(MemberActivity.this, "계정이 성공적으로 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                        navigateToMainActivity();
                    } else {
                        Toast.makeText(MemberActivity.this, "계정 삭제 실패", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(MemberActivity.this, "네트워크 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "사용자 번호가 유효하지 않습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToMainActivity() {
        SessionManager.getInstance(MemberActivity.this).clear();
        // 메인 화면으로 이동하는 코드 작성
        Intent intent = new Intent(MemberActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // 현재 화면을 닫음
    }
}
