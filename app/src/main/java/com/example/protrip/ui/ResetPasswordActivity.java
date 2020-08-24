package com.example.protrip.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.protrip.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText email;
    private Button resetBtn, cancelBtn;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        initUI();
    }

    private void initUI() {

        email = findViewById(R.id.email);
        resetBtn = findViewById(R.id.btn_reset);
        cancelBtn = findViewById(R.id.btn_annuler);

        resetBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);

        firebaseAuth = FirebaseAuth.getInstance();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_reset:
                resetPassword();
                break;

            case R.id.btn_annuler:
                cancelReset();
                break;

        }
    }

    private void cancelReset() {
        startActivity(new Intent(ResetPasswordActivity.this, LoginActivity.class));
        finish();
    }

    private void resetPassword() {
        String mail = email.getText().toString();
        if(mail.equals("")){
            Toast.makeText(ResetPasswordActivity.this, "All files are required", Toast.LENGTH_SHORT).show();
        } else {
            firebaseAuth.sendPasswordResetEmail(mail).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(ResetPasswordActivity.this, "Please check your email", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ResetPasswordActivity.this,LoginActivity.class));
                    } else {
                         String error = task.getException().getMessage();
                         Toast.makeText(ResetPasswordActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this,LoginActivity.class));
    }

}