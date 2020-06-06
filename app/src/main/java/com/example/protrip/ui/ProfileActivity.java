package com.example.protrip.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.protrip.R;
import com.example.protrip.data.User;
import com.example.protrip.util.Constant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView avatar;
    private TextView emailTV;
    private EditText fullNameET;
    private Button saveBtn;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference users = database.getReference(Constant.USERS);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initUI();
        initProfile();
    }

    private void initProfile() {
        users.child(mUser.getUid())
             .addValueEventListener(new ValueEventListener() {

                 @Override
                 public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 User usr = dataSnapshot.getValue(User.class);

                 if(usr != null){
                     fullNameET.setText(usr.getFullName()); // usr?.getFullName()
                 }

                 }
                 @Override
                 public void onCancelled(@NonNull DatabaseError databaseError) {

                 }
             });
    }

    private void initUI() {
        avatar = findViewById(R.id.avatar);
        emailTV = findViewById(R.id.email);
        fullNameET = findViewById(R.id.full_name);
        saveBtn = findViewById(R.id.save);

        saveBtn.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.save :
                updateProfile();
                break;
        }
    }

    private void updateProfile() {

        String fullName = fullNameET.getText().toString();

        // Construct user
        User usr = new User(mUser.getEmail(),fullName,null);

        users.child(mUser.getUid())
             .setValue(usr)
             .addOnCompleteListener(task -> {
                 if(task.isSuccessful()) {
                     Toast.makeText(ProfileActivity.this, "Profile updated", Toast.LENGTH_LONG).show();
                 }
             });
    }
}