package com.project.cameradirectory;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private EditText numberInput;
    private Button loadButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webView);
        numberInput = findViewById(R.id.numberInput);
        loadButton = findViewById(R.id.loadButton);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());

        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = numberInput.getText().toString();
                if (isValidNumber(number)) {
                    String url = "http://rpm-web.p-e.kr/mobile/ride_info.php?targetNo=" + number;
                    webView.loadUrl(url);
                } else {
                    Toast.makeText(MainActivity.this, "Please enter a number between 1 and 4", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean isValidNumber(String number) {
        try {
            int num = Integer.parseInt(number);
            return num >= 1 && num <= 4;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}

