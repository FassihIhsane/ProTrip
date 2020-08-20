package com.example.protrip.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.protrip.R;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView changePassword, deleteAccount,changeEmail;
    private ImageButton back;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initUI();
    }

    private void initUI() {
        changePassword = findViewById(R.id.change_password);
        deleteAccount = findViewById(R.id.delete_account);
        changeEmail = findViewById(R.id.change_email);
        changeEmail.setOnClickListener(this);
        back = findViewById(R.id.back_settings);
        back.setOnClickListener(this);
        toolbar = findViewById(R.id.toolbar_settings);
        setSupportActionBar(toolbar);

        changePassword.setOnClickListener(this);
        deleteAccount.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.change_password:
                updatePassword();
                break;
            case R.id.change_email:
                updateEmail();
                break;

            case R.id.delete_account :
                deleteAccountUser();
                break;
            case R.id.back_settings :
                startActivity(new Intent(this,MapsActivity.class));
                finish();
                break;
    }
}

    private void updateEmail() {
        startActivity(new Intent(this,ChangeEmailActivity.class));
    }

    private void updatePassword() {
        startActivity(new Intent(this, ChangePasswordActivity.class));
    }

    private void deleteAccountUser() {
    }
    }