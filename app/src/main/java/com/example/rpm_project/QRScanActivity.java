package com.example.rpm_project;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.DecoratedBarcodeView.TorchListener;

import java.util.List;

public class QRScanActivity extends AppCompatActivity implements TorchListener {

    private static final String TAG = QRScanActivity.class.getSimpleName();
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1;
    private static final int REQUEST_CODE_CODE_INPUT = 2; // Request code for code input activity
    private DecoratedBarcodeView barcodeView;
    private Button btnCodeInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scan);

        barcodeView = findViewById(R.id.scanner_view);
        btnCodeInput = findViewById(R.id.btn_code_input);

        // 카메라 권한 확인 및 요청
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            startCamera();
        }

        // 코드 입력 버튼 클릭 이벤트 처리
        btnCodeInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 코드 입력 화면으로 이동
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
            Toast.makeText(this, "카메라를 시작하는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private final BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            try {
                String qrCode = result.getText();
                Intent intent = new Intent();
                intent.putExtra("qr_code", qrCode);
                setResult(RESULT_OK, intent);
                finish();
            } catch (Exception e) {
                Log.e(TAG, "Error processing barcode result", e);
                Toast.makeText(QRScanActivity.this, "QR 코드 처리 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
            // Optional callback method
        }
    };

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
                Toast.makeText(this, "카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_CODE_INPUT) {
            if (resultCode == RESULT_OK) {
                // 코드 입력 성공 처리
                String enteredCode = data.getStringExtra("entered_code");
                Toast.makeText(this, "입력한 코드: " + enteredCode, Toast.LENGTH_SHORT).show();
                // 여기서 입력된 코드를 사용하여 원하는 작업을 수행할 수 있습니다.
            } else if (resultCode == RESULT_CANCELED) {
                // 코드 입력 취소 처리
                Toast.makeText(this, "코드 입력이 취소되었습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onTorchOn() {
        // Torch ON 상태
    }

    @Override
    public void onTorchOff() {
        // Torch OFF 상태
    }
}
