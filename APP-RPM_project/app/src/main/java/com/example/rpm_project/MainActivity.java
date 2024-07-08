package com.example.rpm_project;

import static com.example.rpm_project.QRScanActivity.SHARED_PREFS;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private EditText editID, editPassword;
    private Button btnLogin;
    private TextView txtRegister;
    private ApiService apiService;

    private static final String TAG = "MainActivity";

    private static final int REQUEST_CODE_PERMISSION = 100;
    private static final String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.MANAGE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        editID = findViewById(R.id.edit_ID);
        editPassword = findViewById(R.id.edit_Password);
        btnLogin = findViewById(R.id.btn_Login);
        txtRegister = findViewById(R.id.txt_Register);

        // 권한 체크 및 요청
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!checkPermissions()) {
                Log.d(TAG, "Permissions not granted, requesting permissions.");
                requestPermissions();
            } else {
                Log.d(TAG, "Permissions already granted, showing login screen.");
                // 권한이 이미 허용된 경우 로그인 화면 표시
                showLoginScreen();
            }
        } else {
            Log.d(TAG, "SDK version < M, showing login screen.");
            // 안드로이드 버전이 M 미만인 경우에는 바로 로그인 화면 표시
            showLoginScreen();
        }

        // 로그인 버튼 클릭 이벤트 처리
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = editID.getText().toString();
                String pw = editPassword.getText().toString();

                // Retrofit을 사용하여 서버에 로그인 요청 보내기
                Call<ResponseBody> call = apiService.login(id, pw);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            try {
                                String responseBody = response.body().string();
                                if (responseBody.startsWith("Login successful!")) {
                                    // Extract user_no from the response
                                    int userNo = Integer.parseInt(responseBody.split("UserNo: ")[1]);

                                    // 사용자 번호를 세션에 저장
                                    SessionManager.getInstance(MainActivity.this).setUserNo(userNo);

                                    Log.d(TAG, "사용자 번호가 세션에 저장되었습니다. 사용자 번호: " + userNo);
                                    int storedUserNo = SessionManager.getInstance(MainActivity.this).getUserNo();
                                    Log.d(TAG, "세션에 저장된 사용자 번호: " + storedUserNo);

                                    // 로그인 성공 메시지 표시
                                    Toast.makeText(MainActivity.this, "OO랜드에 오신걸 환영합니다", Toast.LENGTH_SHORT).show();

                                    // 로그인 성공 시 TicketActivity로 이동
                                    Intent intent = new Intent(MainActivity.this, TicketActivity.class);
                                    startActivity(intent);
                                    finish(); // 현재 MainActivity 종료
                                } else {
                                    // 서버가 반환한 오류 메시지를 Toast로 표시
                                    Toast.makeText(MainActivity.this, responseBody, Toast.LENGTH_SHORT).show();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        // 네트워크 오류 등으로 인한 로그인 실패
                        Toast.makeText(MainActivity.this, "로그인 실패: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "로그인 실패", t);
                    }
                });
            }
        });

        // 회원가입 텍스트 클릭 이벤트 처리
        txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 회원가입 화면으로 이동
                Intent intent = new Intent(MainActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }

    // 권한 체크
    private boolean checkPermissions() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permission " + permission + " not granted.");
                return false;
            }
        }
        Log.d(TAG, "All permissions granted.");
        return true;
    }

    // 권한 요청
    private void requestPermissions() {
        Log.d(TAG, "Requesting permissions: " + String.join(", ", REQUIRED_PERMISSIONS));
        ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSION);
    }

    // 권한 요청 결과 처리
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION) {
            boolean allGranted = true;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Permission " + permissions[i] + " not granted.");
                    allGranted = false;
                    break;
                }
            }
            if (allGranted) {
                Log.d(TAG, "All permissions granted, showing login screen.");
                // 모든 권한을 허용한 경우 로그인 화면 표시
                showLoginScreen();
            } else {
                Log.d(TAG, "Not all permissions granted, showing permission rationale.");
                // 권한을 허용하지 않은 경우 권한이 필요하다는 메시지를 표시하고 다시 요청
//                showPermissionRationale();
            }
        }
    }

    // 권한 요청 이유를 설명하고 다시 요청하거나 설정으로 이동하는 방법 제공
//    private void showPermissionRationale() {
//        new AlertDialog.Builder(this)
//                .setTitle("권한 요청")
//                .setMessage("앱을 사용하려면 권한을 허용해야 합니다. 설정에서 권한을 허용해주세요.")
//                .setPositiveButton("설정으로 이동", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Log.d(TAG, "Navigating to settings for permission.");
//                        // 설정 화면으로 이동
//                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                        Uri uri = Uri.fromParts("package", getPackageName(), null);
//                        intent.setData(uri);
//                        startActivity(intent);
//                    }
//                })
//                .setNegativeButton("다시 시도", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Log.d(TAG, "Retrying permission request.");
//                        requestPermissions();
//                    }
//                })
//                .setCancelable(false)
//                .show();
//    }

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putBoolean("isQrCodeRegistered", false);
//        editor.apply();
//    }

    // 로그인 화면 표시 메서드
    private void showLoginScreen() {
        Log.d(TAG, "Showing login screen.");
        editID.setVisibility(View.VISIBLE);
        editPassword.setVisibility(View.VISIBLE);
        btnLogin.setVisibility(View.VISIBLE);
        txtRegister.setVisibility(View.VISIBLE);
    }
}