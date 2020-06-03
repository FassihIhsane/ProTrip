package com.example.protrip.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.protrip.R;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Button loginBtn;
    private EditText emailET,passwordET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initUI();

    }

    private void initUI() {
        loginBtn = findViewById(R.id.btn_login);
        emailET = findViewById(R.id.email);
        passwordET = findViewById(R.id.password);
        loginBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_login:
                String email = emailET.getText().toString();
                String password = passwordET.getText().toString();
                boolean isValid = validateInput(email,password);
            break;

        }
    }

    private boolean validateInput(String email, String password) {
        /*
            [a-z0-9]{9,12}@[a-z]{4,5}[.]{1}[a-z]{2,3}
            hgdfhdg124@dhgfdh.com
         */
        boolean isValid = true;

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailET.setError("Invalid email");
            isValid = false;
        }

        if(password.length() < 6){
            passwordET.setError("at least 6 characters");
            isValid = false;
        }

        return isValid;
    }
}
