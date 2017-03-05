package com.rohitdeveloper.connectinsight;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashScreen extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(SPLASH_TIME_OUT);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {

                    Intent intent = new Intent(SplashScreen.this, LocationActivity.class);
                    startActivity(intent);

                }

            }
        }).start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}