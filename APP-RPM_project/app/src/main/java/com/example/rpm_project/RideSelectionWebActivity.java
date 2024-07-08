package com.example.rpm_project;

import static com.example.rpm_project.QRScanActivity.SHARED_PREFS;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;

public class RideSelectionWebActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_selection_web);

        webView = findViewById(R.id.web_view);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());

        // JavaScript 인터페이스 추가
        webView.addJavascriptInterface(new WebAppInterface(this), "Android");

        // EC2 서버의 URL 로드
        webView.loadUrl("http://ec2-3-37-200-50.ap-northeast-2.compute.amazonaws.com/mobile/index.html");
    }

    public class WebAppInterface {
        RideSelectionWebActivity mActivity;

        /** Instantiate the interface and set the context */
        WebAppInterface(RideSelectionWebActivity activity) {
            mActivity = activity;
        }

        /** Show a toast from the web page */
        @JavascriptInterface
        public void goToAndroidActivity() {
            Intent intent = new Intent(mActivity, CameraActivity.class); // 일단 기존에 있던 기구선택창으로 넘어가게 해놨습니다.
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        boolean isQrCodeRegistered = sharedPreferences.getBoolean("isQrCodeRegistered", false);

        Intent intent;
        if (isQrCodeRegistered) {
            // QR 코드가 등록된 경우, 이전 단계로 이동 (자녀 등록 화면으로 이동하는 예제)
            intent = new Intent(this, RegisterPersonActivity.class);
        } else {
            // QR 코드가 등록되지 않은 경우, QR 코드 인식 화면으로 이동
            intent = new Intent(this, QRScanActivity.class);
        }
        startActivity(intent);
        finish();
    }
}