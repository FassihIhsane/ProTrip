package com.example.protrip.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.protrip.R;
import com.example.protrip.data.Trip;
import com.example.protrip.data.User;
import com.example.protrip.util.Constant;
import com.example.protrip.util.DB;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfile extends AppCompatActivity implements View.OnClickListener {

    private Button message;
    private TextView userName, description;
    private ImageButton updateName, updateDescription;
    CircleImageView imageProfile;

    private StorageReference mStorageRef;

    private String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profil);
        initUI();
        initProfile(userId);
    }

    private void initProfile(String idUser) {


        DB.getReference(Constant.USERS).child(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User usr = dataSnapshot.getValue(User.class);

                        if (usr != null) {
                            userName.setText(usr.getFullName());
                            description.setText(usr.getDescription());
                            userName.setEnabled(false);
                            description.setEnabled(false);
                            message.setVisibility(View.VISIBLE);
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
        imageProfile = findViewById(R.id.image_profile);

        message.setOnClickListener(this);
        userId = getIntent().getStringExtra(Constant.USERID_INTENT);

        mStorageRef = FirebaseStorage.getInstance().getReference();
        StorageReference storageReference = mStorageRef.child("users/"+userId+"/profile.jpg");
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(imageProfile);
            }
        });

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.contact_me:
                chatProfile();
                break;
        }
    }

    private void chatProfile() {

        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
    }
}