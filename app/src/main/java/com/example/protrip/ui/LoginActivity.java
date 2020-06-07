package com.example.protrip.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.protrip.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivityTag";
    private Button loginBtn;
    private EditText emailET,passwordET;
    private TextView signup;
    private ProgressBar loginProgress;

    private FirebaseAuth mAuth;

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
        loginProgress = findViewById(R.id.login_progress);
        loginBtn.setOnClickListener(this);

        signup = findViewById(R.id.signup);
        signup.setOnClickListener(this);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_login:
                String email = emailET.getText().toString();
                String password = passwordET.getText().toString();
                boolean isValid = validateInput(email,password);
                if(isValid){
                    loginUser(email, password);
                }
            break;

            case R.id.signup:
                showSignup();
                break;

        }
    }

    private void showSignup() {
        startActivity(new Intent(this, SignupActivity.class));
        finish();
    }

    private void loginUser(String email, String password) {

        //Log.d(TAG, "email:"+email+" | password:password");
        toggleLogin();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        //Log.d(TAG, task.getException().getMessage());

                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                           // Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            startActivity(new Intent(LoginActivity.this, MapsActivity.class));
                            finish();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            toggleLogin();
                        }

                    }
                });
    }

    private void toggleLogin() { // if false set true & vis versa

        if(loginBtn.getVisibility() == View.VISIBLE){
            loginBtn.setVisibility(View.GONE);
        }else{
            loginBtn.setVisibility(View.VISIBLE);
        }

        if(loginProgress.getVisibility() == View.VISIBLE){
            loginProgress.setVisibility(View.GONE);
        }else{
            loginProgress.setVisibility(View.VISIBLE);
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
