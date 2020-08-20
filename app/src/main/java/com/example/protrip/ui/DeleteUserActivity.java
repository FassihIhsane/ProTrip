package com.example.protrip.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

public class DeleteUserActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText email, password;
    private Button deleteUser;
    private ImageButton back;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_user);

        initUI();
    }

    private void initUI() {
        email = findViewById(R.id.email_delete);
        password = findViewById(R.id.password_delete);
        deleteUser = findViewById(R.id.delete_account_btn);
        deleteUser.setOnClickListener(this);
        back = findViewById(R.id.back_user);
        back.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.delete_account_btn :
                deleteAccount();
                break;
            case R.id.back_user :
                backSettings();
                break;
        }
    }

    private void backSettings() {
        startActivity(new Intent(this,SettingsActivity.class));
        finish();
    }

    private void deleteAccount() {
        String mail = email.getText().toString();
        String pass = password.getText().toString();
        FirebaseUser user = mAuth.getCurrentUser();

        if(!mail.isEmpty() && !pass.isEmpty()){
            if(user != null){
                AuthCredential credential = EmailAuthProvider
                        .getCredential(mail, pass);

                user.reauthenticate(credential)
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            Toast.makeText(DeleteUserActivity.this,"Re-Authentication success",Toast.LENGTH_SHORT).show();
                            user.delete().addOnCompleteListener(task1 -> {
                                if(task1.isSuccessful()){
                               DB.getReference(Constant.USERS).child(user.getUid()).removeValue();
                                    Toast.makeText(DeleteUserActivity.this, "Account deleted successfully", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(DeleteUserActivity.this,FirstLaunchActivity.class));
                                    finish();
                                }else{
                                    Toast.makeText(DeleteUserActivity.this, "Account can not be deleted successfully", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else{
                            Toast.makeText(DeleteUserActivity.this,"Invalid email or password",Toast.LENGTH_SHORT).show();
                        }

                    })    ;
            }
        }else{
            Toast.makeText(this, "Please enter all the fields", Toast.LENGTH_SHORT).show();
        }
    }
}