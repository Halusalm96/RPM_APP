package com.example.rpm_project;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.time.LocalDate;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {

    private EditText editTextID, editTextPassword, editTextPasswordConfirm, editTextName, editTextBirthYear, editTextBirthMonth, editTextBirthDay, editTextEmail, editTextPhoneNumber;
    private Button buttonSignup, buttonPasswordCheck;
    private ApiService apiService;

    private static final String TAG = "SignupActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        editTextID = findViewById(R.id.signID);
        editTextPassword = findViewById(R.id.signPW);
        editTextPasswordConfirm = findViewById(R.id.signPW2);
        editTextName = findViewById(R.id.signName);
        editTextBirthYear = findViewById(R.id.signBirth);
        editTextBirthMonth = findViewById(R.id.signBirth2);
        editTextBirthDay = findViewById(R.id.signBirth3);
        editTextEmail = findViewById(R.id.signmail);
        editTextPhoneNumber = findViewById(R.id.signphone);
        buttonSignup = findViewById(R.id.signupbutton);
        buttonPasswordCheck = findViewById(R.id.pwcheckbutton);

        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = editTextID.getText().toString();
                String pw = editTextPassword.getText().toString();
                String passwordConfirm = editTextPasswordConfirm.getText().toString();
                String name = editTextName.getText().toString();
                String birthYear = editTextBirthYear.getText().toString();
                String birthMonth = editTextBirthMonth.getText().toString();
                String birthDay = editTextBirthDay.getText().toString();
                String email = editTextEmail.getText().toString();
                String number = editTextPhoneNumber.getText().toString();

                // 입력 값 유효성 검사
                if (TextUtils.isEmpty(id) || TextUtils.isEmpty(pw) || TextUtils.isEmpty(passwordConfirm) ||
                        TextUtils.isEmpty(name) || TextUtils.isEmpty(birthYear) || TextUtils.isEmpty(birthMonth) ||
                        TextUtils.isEmpty(birthDay) || TextUtils.isEmpty(email) || TextUtils.isEmpty(number)) {
                    showAlert("모든 필드를 입력하세요.");
                    return;
                }

                if (!pw.equals(passwordConfirm)) {
                    showAlert("비밀번호가 일치하지 않습니다.");
                    return;
                }

                if (!isValidPassword(pw)) {
                    showAlert("비밀번호는 최소 8자 이상, 숫자와 문자를 포함해야 합니다.");
                    return;
                }

                if (!isValidEmail(email)) {
                    showAlert("올바른 이메일 형식이 아닙니다.");
                    return;
                }

                if (!isValidPhoneNumber(number)) {
                    showAlert("올바른 전화번호 형식이 아닙니다.");
                    return;
                }

                // 생년월일 형식 변환
                try {
                    int year = Integer.parseInt(birthYear);
                    int month = Integer.parseInt(birthMonth);
                    int day = Integer.parseInt(birthDay);
                    LocalDate birth = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        birth = LocalDate.of(year, month, day);
                    }

                    // Retrofit을 사용하여 회원가입 요청 보내기
                    ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
                    Call<Void> call = apiService.signup(id, pw, name, birth, email, number);
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            showAlert("회원가입 중 오류 발생: " + t.getMessage());
                            Log.e(TAG, "회원가입 중 오류 발생", t);
                        }
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(SignupActivity.this, "회원가입 성공", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish(); // 현재 SignupActivity 종료
                            } else {
                                showAlert("회원가입 실패");
                                try {
                                    String errorBody = response.errorBody().string();
                                    Log.e(TAG, "회원가입 실패: " + errorBody); // 터미널에 실패 이유 출력
                                } catch (IOException e) {
                                    Log.e(TAG, "회원가입 실패: 오류 내용 파싱 중 예외 발생", e);
                                }
                            }
                        }
                    });
                } catch (NumberFormatException e) {
                    showAlert("생년월일을 올바른 숫자 형식으로 입력하세요.");
                }
            }
        });

        // 비밀번호 일치 검사
        buttonPasswordCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = editTextPassword.getText().toString();
                String passwordConfirm = editTextPasswordConfirm.getText().toString();

                if (password.equals(passwordConfirm)) {
                    showAlert("비밀번호가 일치합니다.");
                } else {
                    showAlert("비밀번호가 일치하지 않습니다.");
                }
            }
        });
    }

    // AlertDialog로 메시지 보여주기
    private void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setPositiveButton("확인", null)
                .create()
                .show();
    }

    // 비밀번호 유효성 검사
    private boolean isValidPassword(String password) {
        // 비밀번호는 최소 8자 이상, 숫자와 문자 포함
        String passwordPattern = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=\\S+$).{8,}$";
        return Pattern.matches(passwordPattern, password);
    }

    // 이메일 유효성 검사
    private boolean isValidEmail(String email) {
        // Android의 TextUtils를 사용하여 이메일 형식 검사
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // 전화번호 유효성 검사
    private boolean isValidPhoneNumber(String phoneNumber) {
        // 전화번호 형식: 숫자만 허용하고, 10자리 또는 11자리 (한국 번호 기준)
        String phonePattern = "^\\d{10,11}$";
        return Pattern.matches(phonePattern, phoneNumber);
    }
}
