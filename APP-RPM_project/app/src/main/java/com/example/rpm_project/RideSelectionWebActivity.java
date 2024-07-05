package com.example.rpm_project;

import android.content.Intent;
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
        webView.addJavascriptInterface(new WebAppInterface(), "Android");

        // EC2 서버의 URL 로드
        webView.loadUrl("http://ec2-3-37-200-50.ap-northeast-2.compute.amazonaws.com/mobile/index.html");
    }

    public class WebAppInterface {

        /** Show a toast from the web page */
        @JavascriptInterface
        public void goToAndroidActivity() {
            Intent intent = new Intent(RideSelectionWebActivity.this, CameraActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
