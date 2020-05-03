package com.project1.heydoc.Login;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;

import com.project1.heydoc.R;

public class Loading_activity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        setTheme(android.R.style.Theme_NoTitleBar);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Log.i("태그", "온 크리에이트");
        setContentView(R.layout.loading);

        Log.i("태그", "셋 컨텐트뷰 로딩 후, 스타트 로딩 전");
        startLoading();
        Log.i("태그", "스타트 로딩 후");
    }

    private void startLoading(){

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), Login_activity.class);
                startActivity(intent);
                finish();
            }
        }, 2000);
    }

}
