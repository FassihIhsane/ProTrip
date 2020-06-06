package com.example.protrip.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.protrip.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MapActivity extends AppCompatActivity {

    private TextView emailTV;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        initUI();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.auth_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.profil:
                showProfil();
                return true;
            case R.id.logout:
                logout();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void logout() {
        mAuth.signOut();
        startActivity(new Intent(this,FirstLunchActivity.class));
        finish();
    }

    private void showProfil() {

        startActivity(new Intent(this,ProfileActivity.class));
    }

    private void initUI() {
        emailTV = findViewById(R.id.email);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if(user!=null)
        emailTV.setText(user.getEmail());
    }
}