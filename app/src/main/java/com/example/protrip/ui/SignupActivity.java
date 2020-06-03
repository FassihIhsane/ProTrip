package com.example.protrip.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.protrip.R;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText fullNameET, passwordET, emailET;
    private Button registerBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        initUI();
    }

    private void initUI() {

        fullNameET = findViewById(R.id.full_name);
        passwordET = findViewById(R.id.password);
        emailET = findViewById(R.id.email);
        registerBtn = findViewById(R.id.btn_register);

        registerBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btn_register :
                String fullName = fullNameET.getText().toString();
                String email = emailET.getText().toString();
                String password = passwordET.getText().toString();
                boolean isValid = validateInput(fullName,email,password);
                break;
        }
    }

    private boolean validateInput(String fullName, String email, String password) {

        boolean isValid = true;

        if(!fullName.trim().matches("[a-zA-Z ]{4,}")) {
            fullNameET.setError("Invalid name");
            isValid = false;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailET.setError("Invalid email");
            isValid = false;
        }

        if(password.length() < 6) {
            passwordET.setError("At least 6 characters");
            isValid = false;
        }

        return isValid;
    }
}