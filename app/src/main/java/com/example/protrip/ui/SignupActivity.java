package com.example.protrip.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.protrip.R;
import com.example.protrip.data.User;
import com.example.protrip.util.Constant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText fullNameET, passwordET, emailET;
    private Button registerBtn;
    private ProgressBar signupProgress;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference users = database.getReference(Constant.USERS);

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
        signupProgress = findViewById(R.id.signup_progress);
        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btn_register :
                signupUser();
                break;
        }
    }

    private void signupUser() {
        String fullName = fullNameET.getText().toString();
        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();
        boolean isValid = validateInput(fullName,email,password);
        if(isValid){
            toggleSignup();
            User usr = new User(email, fullName,null);
            usr.setPassword(password);
            createAccount(usr);
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

    private void createAccount(@NonNull final User usr) {

        mAuth.createUserWithEmailAndPassword(usr.getEmail(), usr.getPassword())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's informations
                            FirebaseUser user = mAuth.getCurrentUser();
                            if(user != null){
                                usr.setPassword(null); // To make sure never store the user's password
                                users.child(user.getUid())
                                     .setValue(usr);

                                toggleSignup();
                                showDialog("Account created successfully!");
                            }


                           // show dialog account created successfully
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(SignupActivity.this, "Error occurred.", Toast.LENGTH_SHORT).show();
                           toggleSignup();
                        }

                        // ...
                    }
                });
    }

    private void showDialog(String msg) {

        new AlertDialog.Builder(this)
                .setTitle("Account created")
                .setMessage(msg)
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    startActivity(new Intent(this,MapsActivity.class));
                    finish();
                })
                .setIcon(R.drawable.ic_check)
                .show();
    }

    private void toggleSignup() { // if false set true & vis versa

        /*if(loginBtn.getVisibility() == View.VISIBLE){
            loginBtn.setVisibility(View.GONE);
        }else{
            loginBtn.setVisibility(View.VISIBLE);
        }

        if(loginProgress.getVisibility() == View.VISIBLE){
            loginProgress.setVisibility(View.GONE);
        }else{
            loginProgress.setVisibility(View.VISIBLE);
        }*/

        // Ternary operator
         registerBtn.setVisibility(
                 (registerBtn.getVisibility() == View.VISIBLE) ? View.GONE : View.VISIBLE
         );
         signupProgress.setVisibility(
                 (signupProgress.getVisibility() == View.VISIBLE) ? View.GONE : View.VISIBLE
         );


    }

}