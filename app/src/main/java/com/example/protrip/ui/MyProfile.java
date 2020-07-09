package com.example.protrip.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.protrip.R;
import com.example.protrip.data.User;
import com.example.protrip.util.Constant;
import com.example.protrip.util.DB;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class MyProfile extends AppCompatActivity implements View.OnClickListener {

    private Button message;
    private TextView userName, description;
    private ImageButton updateName,updateDescription;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profil);
        initUI();
        initProfile();
    }

    private void initProfile() {
        DB.getReference(Constant.USERS).child(DB.getUserId())
                .addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User usr = dataSnapshot.getValue(User.class);

                        if (usr != null ) {
                            userName.setText(usr.getFullName());
                            description.setText(usr.getDescription());
                            updateDescription.setVisibility(View.VISIBLE);
                            updateName.setVisibility(View.VISIBLE);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


    private void initUI() {

        userName = findViewById(R.id.user_name);
        description = findViewById(R.id.description);
        message = findViewById(R.id.contact_me);
        updateName = findViewById(R.id.n_change);
        updateDescription = findViewById(R.id.d_change);

        message.setOnClickListener(this);
        updateName.setOnClickListener(this);
        updateDescription.setOnClickListener(this);



    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.n_change :
                updateName();
                break;
            case R.id.d_change :
                updateDescription();
                break;
    }
}

    private void updateDescription() {
        String desc = description.getText().toString();
        String fullName = userName.getText().toString();
        // Construct user
        User usr = new User(DB.getUserEmail(),fullName,null,desc);

        DB.getReference(Constant.USERS).child(DB.getUserId())
                .setValue(usr)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        Toast.makeText(MyProfile.this, "description updated", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void updateName() {
        String desc = description.getText().toString();
        String fullName = userName.getText().toString();

        // Construct user
        User usr = new User(DB.getUserEmail(),fullName,null,desc);

        DB.getReference(Constant.USERS).child(DB.getUserId())
                .setValue(usr)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        Toast.makeText(MyProfile.this, "Name updated", Toast.LENGTH_LONG).show();
                    }
                });
    }


}
