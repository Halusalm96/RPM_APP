package com.example.rpm_project;

import static com.example.rpm_project.RetrofitClient.*;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QRScanActivity extends AppCompatActivity {

    private static final String TAG = QRScanActivity.class.getSimpleName();
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1;
    private static final int REQUEST_CODE_CODE_INPUT = 2;
    private DecoratedBarcodeView barcodeView;
    private Button btnCodeInput;
    private ApiService apiService;
    private static final String BASE_URL = "http://192.168.123.17:8080/";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scan);

        // Initialize apiService using RetrofitClient
        apiService = RetrofitClient.getClient(BASE_URL).create(ApiService.class);

        barcodeView = findViewById(R.id.scanner_view);
        btnCodeInput = findViewById(R.id.btn_code_input);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            startCamera();
        }

        btnCodeInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QRScanActivity.this, CodeInputActivity.class);
                startActivityForResult(intent, REQUEST_CODE_CODE_INPUT);
            }
        });
    }

    private void startCamera() {
        try {
            barcodeView.decodeContinuous(callback);
        } catch (Exception e) {
            Log.e(TAG, "Error starting camera", e);
            Toast.makeText(this, "Error starting camera", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private final BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            try {
                String qrCode = result.getText();
                if (qrCode != null) {
                    handleQRCode(qrCode);
                }else {
                    Toast.makeText(QRScanActivity.this, "Failed to read QR code", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.e(TAG, "Error processing barcode result", e);
                Toast.makeText(QRScanActivity.this, "Error processing barcode result", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
            // Optional callback method
        }
    };

    private void handleQRCode(String qrCode) {
        Log.d(TAG, "QR Code Scanned: " + qrCode);

        String extractedData = extractDataFromQRCode(qrCode);

        if (extractedData == null || extractedData.isEmpty()) {
            Toast.makeText(QRScanActivity.this, "Failed to read QR code", Toast.LENGTH_SHORT).show();
            return;
        }

//        if (!(qrCode.toLowerCase().startsWith("http://") || qrCode.toLowerCase().startsWith("https://"))) {
//            Toast.makeText(QRScanActivity.this, "Invalid QR Code format", Toast.LENGTH_SHORT).show();
//            return;
//        }

        // Log the extracted data to terminal (Logcat)
        Log.d(TAG, "Extracted Code Data: " + extractedData);

//        ApiService apiService = RetrofitClient.getClient(BASE_URL).create(ApiService.class);
        Call<ResponseBody> call = apiService.validateCode(extractedData);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String message = response.body().string();
                        // 응답에 따른 처리
                        if (message.equals("Welcome to the land!")) {
                            Toast.makeText(QRScanActivity.this, "코드 검증 성공", Toast.LENGTH_SHORT).show();
                            // 인원정보 등록 페이지로 이동 또는 필요한 작업 수행
                            Intent intent = new Intent(QRScanActivity.this, RegisterPersonActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(QRScanActivity.this, "유효하지 않은 코드입니다.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(QRScanActivity.this, "서버 응답 처리 중 오류 발생", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(QRScanActivity.this, "서버 응답 처리 중 오류 발생", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // 실패 처리
                Log.e(TAG, "Error: " + t.getMessage());
                Toast.makeText(QRScanActivity.this, "서버 연결 중 오류 발생", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to extract data from QR code (custom logic based on QR format)
    private String extractDataFromQRCode(String qrCode) {
        // Example: Assuming qrCode is in format http://192.168.123.17:8080/validate_code?code=z1x2c3
        // Extract the 'code' parameter value from the URL
        try {
            String[] parts = qrCode.split("code=");
            if (parts.length > 1) {
                return parts[1];
            }
        } catch (Exception e) {
            Log.e(TAG, "Error extracting data from QR code", e);
        }
        return null;
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            barcodeView.pause();
        } catch (Exception e) {
            Log.e(TAG, "Error pausing camera", e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            barcodeView.resume();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_CODE_INPUT) {
            if (resultCode == RESULT_OK && data != null) {
                String enteredCode = data.getStringExtra("entered_code");
                Toast.makeText(this, "Entered code: " + enteredCode, Toast.LENGTH_SHORT).show();
                // Additional processing using enteredCode if needed
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Code input canceled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        barcodeView.pauseAndWait();
    }
}