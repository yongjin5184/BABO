package com.example.androidtest;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {

    private static final String TAG = "LocalBrowser";
    private final Handler handler = new Handler();
    private WebView webView;
    private TextView textView;
    private Button button;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 화면에서 안드로이드 컨트롤 찾기
        webView = (WebView) findViewById(R.id.webView1);
        textView = (TextView) findViewById(R.id.textView1);
        button = (Button) findViewById(R.id.button1);

        // 임베딩된 브라우저-WebView-에 자바스크립트 활성화하기
        webView.getSettings().setJavaScriptEnabled(true);

        // 자바 객체-AndroidBridge-를 브라우저의 자바 스크립트에 노출시키기
        // 객체, 노출할 DOM명칭
        webView.addJavascriptInterface(new AndroidBridge(), "android");

        // 임의의 WebChromeClient 객체를 생성하여 WebView에 등록.
        // 여기의 Chrome은 브라우저 창 주의를 정리하는 모든 작업을 일컬음. 즉, 브라우저 처리을 할 클라이언트를 등록
        webView.setWebChromeClient(new WebChromeClient() {

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
//                return super.onJsAlert(view, url, message, result);
                Log.d(TAG, "onJsAlert(" + view + ", " + url + ", " + message + ", " + result + ")");

                Toast.makeText(MainActivity.this, message, 3000).show();

                result.confirm(); // 결과 처리됨
                return true; // 여기서 처리했으므로 true 반환
            }
        });

        // 로컬 assets에서 웹 페이지 로딩하기
        webView.loadUrl("file:///android_asset/login.html");

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick(" + v + ")");

                // 자바스크립트 호출
                webView.loadUrl("javascript:callJS('Hello from Android')");
            }
        });
        
        
    }
    private class AndroidBridge {
        public void callAndroid(final String arg) { // 반드시 final이어야 함

            handler.post(new Runnable() {

                @Override
                public void run() {
                    Log.d(TAG, "callAndroid(" + arg + ")");

                    textView.setText(arg);
                }
            });
        }
    }
}

