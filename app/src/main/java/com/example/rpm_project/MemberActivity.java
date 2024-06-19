package com.example.rpm_project;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MemberActivity extends AppCompatActivity {

    private ApiService apiService;
    private TextView txtUserID, txtUserName, txtUserBirth, txtUserEmail, txtUserPhone;
    private Button btnEdit;
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

        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        int userNo = sharedPreferences.getInt("user_no", -1); // int 타입으로 가져오기
        userPassword = sharedPreferences.getString("user_pw", "");

        if (userNo != -1) {
            getUserInfo(userNo);
        } else {
            Toast.makeText(this, "사용자 번호가 유효하지 않습니다.", Toast.LENGTH_SHORT).show();
        }

        btnEdit.setOnClickListener(v -> showPasswordDialog());
    }

    private void getUserInfo(int userNo) {
        Call<User> call = apiService.getUser(userNo);
        Log.d(TAG, "네트워크 요청: " + call.request().toString());
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Log.d(TAG, "네트워크 응답: " + response.toString());
                Log.d(TAG, "서버 응답 코드: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    Log.d(TAG, "응답 본문 (성공): " + user.toString());
                    handleUserData(user);
                } else {
                    handleUserInfoFailure(response);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(MemberActivity.this, "사용자 정보를 가져오는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "사용자 정보 가져오기 실패", t);
            }
        });
    }

    private void handleUserData(User user) {
        if (user != null) {
            String id = user.getUserId() != null ? user.getUserId() : "사용자 ID 없음";
            String name = user.getUserName() != null ? user.getUserName() : "사용자 이름 없음";
            String birth = user.getUserBirth() != null ? formatDate(user.getUserBirth()) : "생일 정보 없음";
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

    // LocalDate를 yyyy-MM-dd 형식의 문자열로 변환하는 메서드
    private String formatDate(LocalDate date) {
        if (date != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return date.format(formatter);
        } else {
            return "날짜 없음";
        }
    }


    private void handleUserInfoFailure(Response<User> response) {
        try {
            String errorBody = response.errorBody() != null ? response.errorBody().string() : "null";
            Log.e(TAG, "응답 본문 (오류): " + errorBody);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "사용자 정보를 가져오지 못했습니다. 오류 응답 코드: " + response.code());
        Toast.makeText(MemberActivity.this, "사용자 정보를 가져오지 못했습니다.", Toast.LENGTH_SHORT).show();
    }

    private void showPasswordDialog() {
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
                        if (enteredPassword.equals(userPassword)) {
                            Toast.makeText(MemberActivity.this, "비밀번호가 확인되었습니다. 정보를 수정하세요.", Toast.LENGTH_SHORT).show();
                            updateUserInfo(); // 정보 수정 메서드 호출
                        } else {
                            Toast.makeText(MemberActivity.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("취소", null)
                .create()
                .show();
    }

    private void updateUserInfo() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        int userNo = sharedPreferences.getInt("user_no", -1); // int 타입으로 가져오기

        if (userNo != -1) {
            User updatedUser = new User();
            updatedUser.setUserName(txtUserName.getText().toString());
            updatedUser.setUserBirth(LocalDate.parse(txtUserBirth.getText().toString())); // String을 LocalDate로 변환
            updatedUser.setUserEmail(txtUserEmail.getText().toString());
            updatedUser.setUserNumber(txtUserPhone.getText().toString());

            Call<User> call = apiService.updateUser(userNo, updatedUser);
            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(MemberActivity.this, "사용자 정보가 성공적으로 수정되었습니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MemberActivity.this, "사용자 정보 수정에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Toast.makeText(MemberActivity.this, "사용자 정보 수정 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "사용자 정보 수정 실패", t);
                }
            });
        } else {
            Toast.makeText(this, "사용자 번호가 유효하지 않습니다.", Toast.LENGTH_SHORT).show();
        }
    }
}
