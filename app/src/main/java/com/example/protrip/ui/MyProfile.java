package com.example.protrip.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.protrip.R;
import com.example.protrip.data.User;
import com.example.protrip.util.Constant;
import com.example.protrip.util.DB;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;


import de.hdodenhof.circleimageview.CircleImageView;

public class MyProfile extends AppCompatActivity implements View.OnClickListener {

    private Button message;
    private TextView userName, description;
    private ImageButton updateName,updateDescription, updatePicture;
    CircleImageView imageProfile;

    private StorageReference mStorageRef;

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
                            updatePicture.setVisibility(View.VISIBLE);
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
        imageProfile = findViewById(R.id.image_profile);
        updatePicture = findViewById(R.id.i_change);

        message.setOnClickListener(this);
        updateName.setOnClickListener(this);
        updateDescription.setOnClickListener(this);
        updatePicture.setOnClickListener(this);

        mStorageRef = FirebaseStorage.getInstance().getReference();
        StorageReference storageReference = mStorageRef.child("users/"+DB.getUserId()+"/profile.jpg");
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

            case R.id.n_change :
                updateName();
                break;
            case R.id.d_change :
                updateDescription();
                break;
            case R.id.i_change :
                openGallery();
                break;
    }
}

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1000);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000){
            if(resultCode == Activity.RESULT_OK){
                Uri imageUri = data.getData();
                imageProfile.setImageURI(imageUri);

                uploadImageToFirebase(imageUri);
            }
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        StorageReference fileReference = mStorageRef.child("users/"+DB.getUserId()+"/profile.jpg");
        fileReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(imageProfile);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MyProfile.this,"Failed",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateDescription() {
        String desc = description.getText().toString();
        String fullName = userName.getText().toString();
        // Construct user
        User usr = new User(DB.getUserEmail(), fullName,desc);
        DB.getReference(Constant.USERS).child(DB.getUserId())
                .setValue(usr)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(MyProfile.this, "description updated", Toast.LENGTH_LONG).show();
                    }

                });
    }

    private void updateName() {
        String desc = description.getText().toString();
        String fullName = userName.getText().toString();

        // Construct user
        User usr = new User(DB.getUserEmail(),fullName,desc);

        DB.getReference(Constant.USERS).child(DB.getUserId())
                .setValue(usr)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        Toast.makeText(MyProfile.this, "Name updated", Toast.LENGTH_LONG).show();
                    }
                });

    }
}
