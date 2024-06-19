package com.example.rpm_project;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextID;
    private EditText editTextPassword;
    private Button btn_Register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_id_find);

//        editTextID = findViewById(R.id.editTextID);
//        editTextPassword = findViewById(R.id.editTextPassword);
//        btn_Register = findViewById(R.id.btn_Register);

        btn_Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editTextID.getText().toString();
                String password = editTextPassword.getText().toString();

                if (!username.isEmpty() && !password.isEmpty()) {
                    // RegisterTask 실행
//                    new RegisterTask(RegisterActivity.this).execute(username, password);
                } else {
                    Toast.makeText(RegisterActivity.this, "아이디와 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
