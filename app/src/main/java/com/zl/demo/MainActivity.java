package com.zl.demo;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.zl.netlib.CronetUrlRequestCallback;

import org.chromium.net.CronetEngine;
import org.chromium.net.UrlRequest;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private static CronetEngine mCronetEngine;
    private static Executor mExecutor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UrlRequest.Builder requestBuilder = mCronetEngine.newUrlRequestBuilder("https://ssl.gstatic.com/gb/images/qi2_00ed8ca1.png", new CronetUrlRequestCallback(), mExecutor);
                UrlRequest request = requestBuilder.build();
                request.start();
            }
        });
        mCronetEngine = new CronetEngine.Builder(this)
                .enableBrotli(true)
                .enableHttp2(true)
                .enableHttpCache(CronetEngine.Builder.HTTP_CACHE_IN_MEMORY, 1024 * 100)
                .enableQuic(true)
                .build();
    }
}