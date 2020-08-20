package com.example.protrip.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class ChangeEmailActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText currentEmail, newEmail, password;
    Button changeEmail;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email);

        initUI();
    }

    private void initUI() {
        currentEmail = findViewById(R.id.current_email);
        newEmail = findViewById(R.id.new_email);
        password = findViewById(R.id.password);
        changeEmail = findViewById(R.id.change_email_btn);
        changeEmail.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.back_email:
                backSettings();
                break;
            case R.id.change_email_btn :
                String email = newEmail.getText().toString();
                boolean isValid = validateInput(email);
                if(isValid) {
                    changeEmailUser(email);
                    break;
                }
        }
    }

    private void backSettings() {
        startActivity(new Intent(this, SettingsActivity.class));
        finish();
    }

    private void changeEmailUser(String email) {
        FirebaseUser mUser = mAuth.getCurrentUser();
        String mail = currentEmail.getText().toString();
        String pass = password.getText().toString();

        if (!mail.isEmpty() && !pass.isEmpty() && !email.isEmpty()) {
            if (mUser != null) {
                if (mUser != null) {
                    AuthCredential credential = EmailAuthProvider
                            .getCredential(mail, pass);

                    mUser.reauthenticate(credential).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(ChangeEmailActivity.this, "Re-Authentication success.", Toast.LENGTH_SHORT).show();
                            mUser.updateEmail(email)
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                           DB.getReference(Constant.USERS).child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    User user = snapshot.getValue(User.class);
                                                    assert user != null;
                                                    user.setEmail(email);
                                                    DB.getReference(Constant.USERS)
                                                      .child(mUser.getUid())
                                                      .setValue(user);
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                            Toast.makeText(ChangeEmailActivity.this, "Email changed successfully.", Toast.LENGTH_SHORT).show();
                                            mAuth.signOut();
                                            startActivity(new Intent(ChangeEmailActivity.this, LoginActivity.class));
                                            finish();
                                        } else {
                                            Toast.makeText(ChangeEmailActivity.this, "Email can not be changed.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(ChangeEmailActivity.this, "Re-Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            } else {
                Toast.makeText(this, "Please enter all the fields", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Boolean validateInput(String email){

        boolean isValid = true;

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            newEmail.setError("Invalid email");
            isValid = false;
        }

        return isValid;
    }
}