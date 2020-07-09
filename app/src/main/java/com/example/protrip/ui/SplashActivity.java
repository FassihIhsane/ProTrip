package com.example.protrip.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.protrip.R;
import com.example.protrip.util.Constant;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Handler handler = new Handler();
        /*handler.postDelayed(new Runnable() { // LAMBDA = Anonymous function () -> 1.8 | Only functional interfaces
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, FirstLunchActivity.class);
                startActivity(intent);
                finish();
            }
        }, Constant.SPLASH_DELAY);*/

        handler.postDelayed(() -> {

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            Class<?> activity = (user != null) ? MapsActivity.class : FirstLaunchActivity.class;

            Intent intent = new Intent(SplashActivity.this, activity);
            startActivity(intent);
            finish();
        }, Constant.SPLASH_DELAY);

    }
}
