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
}