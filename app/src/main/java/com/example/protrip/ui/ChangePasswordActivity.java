package com.example.protrip.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.protrip.R;
import com.example.protrip.data.User;
import com.example.protrip.util.Constant;
import com.example.protrip.util.DB;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class ChangePasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText currentPassword, newPassword, confirmPassword;
    private Button changePassword;
    private ImageButton back;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        initUI();
    }

    private void initUI() {
        currentPassword = findViewById(R.id.current_password);
        newPassword = findViewById(R.id.new_password);
        confirmPassword = findViewById(R.id.confirmation);
        back = findViewById(R.id.back_password);
        back.setOnClickListener(this);
        changePassword = findViewById(R.id.change_password_btn);
        changePassword.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.back_password:
                backSettings();
                break;

            case R.id.change_password_btn :
                String password = newPassword.getText().toString();
                boolean isValid = validateInput(password);
                if(isValid) {
                    changePasswordUser(password);
                }
                break;
    }
}

    private void changePasswordUser(String password) {
        FirebaseUser mUser = mAuth.getCurrentUser();
        String currentPass = currentPassword.getText().toString();
        String confirmPass = confirmPassword.getText().toString();
        if(!currentPass.isEmpty() && !confirmPass.isEmpty() && !password.isEmpty()){
            if(confirmPass.equals(password)){

                if(mUser != null){
                    AuthCredential credential = EmailAuthProvider
                            .getCredential(Objects.requireNonNull(mUser.getEmail()), currentPass);

                    mUser.reauthenticate(credential)
                            .addOnCompleteListener(task -> {
                                if(task.isSuccessful()){
                                    Toast.makeText(ChangePasswordActivity.this, "Re-Authentication success.", Toast.LENGTH_SHORT).show();
                                    mUser.updatePassword(password)
                                            .addOnCompleteListener(task1 -> {
                                                if (task1.isSuccessful()) {
                                                    Toast.makeText(ChangePasswordActivity.this, "Password changed successfully.", Toast.LENGTH_SHORT).show();
                                                    mAuth.signOut();
                                                    startActivity(new Intent(ChangePasswordActivity.this,LoginActivity.class));
                                                    finish();

                                                }else{
                                                    Toast.makeText(ChangePasswordActivity.this, "Password can not be changed.", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }else {
                                    currentPassword.setError("Invalid password");
                                }
                            });

                }else{
                    startActivity(new Intent(this,LoginActivity.class));
                    finish();
                }
                
            }else{
                confirmPassword.setError("Password mismatching");
            }
            
        }else{
            Toast.makeText(this, "Please enter all the fields", Toast.LENGTH_SHORT).show();
        }
                    }

    private void backSettings() {
        startActivity(new Intent(this, SettingsActivity.class));
        finish();
    }

    private Boolean validateInput(String password){

        boolean isValid = true;

        if(password.length() < 6){
            newPassword.setError("At least 6 characters");
            isValid = false;
        }

        return isValid;
        }
    }
