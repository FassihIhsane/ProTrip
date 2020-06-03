package com.example.protrip.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.protrip.R;

public class FirstLunchActivity extends AppCompatActivity implements View.OnClickListener {

    private Button login, register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_lunch);

        initUI();
    }

    private void initUI() {
        login = findViewById(R.id.btn_login);
        register= findViewById(R.id.btn_signup);
        login.setOnClickListener(this);
        register.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_login:
                startActivity(new Intent(this,LoginActivity.class) );
                break;

            case R.id.btn_signup:
                startActivity(new Intent(this,SignupActivity.class) );
                break;

        }
    }
}
