package com.example.protrip.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.protrip.R;
import com.example.protrip.data.ListFriend;
import com.example.protrip.data.Trip;
import com.example.protrip.data.User;
import com.example.protrip.util.Constant;
import com.example.protrip.util.DB;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfile extends AppCompatActivity implements View.OnClickListener {


    private TextView userName, description,age,tel,mail,status;
    private ImageButton message,back,add;
    private ImageView online,offline;
    CircleImageView imageProfile;
    private Toolbar toolbar;
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
        DB.getReference(Constant.USERS).child(idUser)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User usr = dataSnapshot.getValue(User.class);
                        ListFriend listFriend = dataSnapshot.getValue(ListFriend.class);
                        if (usr != null) {
                            userName.setText(usr.getFullName());
                            description.setText(usr.getDescription());
                            mail.setText(usr.getEmail());
                            tel.setText(usr.getTel());
                            age.setText(usr.getAge());
                            userName.setEnabled(false);
                            description.setEnabled(false);
                            add.setVisibility(View.VISIBLE);
                            manageButton(idUser);
                            if (usr.getStatus().equals("online")) {
                                online.setVisibility(View.VISIBLE);
                                offline.setVisibility(View.GONE);
                                status.setText(usr.getStatus());
                            } else {
                                offline.setVisibility(View.VISIBLE);
                                online.setVisibility(View.GONE);
                                status.setText(usr.getStatus());
                            }



                        }}
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                }

    private void manageButton(String idUser) {
        DB.getReference(Constant.FRIENDS).child(DB.getUserId()).child(idUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild("userId")){
                    String user = Objects.requireNonNull(snapshot.child("userId").getValue()).toString();
                    if(user.equals(idUser)){
                        message.setVisibility(View.VISIBLE);
                        add.setVisibility(View.GONE);
                    }else{
                        add.setVisibility(View.VISIBLE);
                    }
                    }
                }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkOnlineStatus(String status){
       DatabaseReference setChild = DB.getReference(Constant.USERS).child(DB.getUserId());
       setChild.child("status").setValue(status);
    }


    private void initUI() {

        userName = findViewById(R.id.user_name);
        description = findViewById(R.id.description);
        tel = findViewById(R.id.tel_text);
        mail = findViewById(R.id.mail_text);
        age = findViewById(R.id.age_text);
        message = findViewById(R.id.contact_me);
        imageProfile = findViewById(R.id.image_profile);
        online = findViewById(R.id.online_button);
        offline = findViewById(R.id.offline_button);
        add = findViewById(R.id.add_me);
        add.setOnClickListener(this);
        status = findViewById(R.id.status);
        back = findViewById(R.id.back_btn);
        back.setOnClickListener(this);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
            case R.id.back_btn:
                startActivity(new Intent(UserProfile.this,MapsActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                break;
            case R.id.add_me:
                addFriend();
                break;
        }
    }

    private void addFriend() {

        ListFriend listFriend = new ListFriend("saved",userId);
        DB.getReference(Constant.FRIENDS)
                       .child(DB.getUserId())
                       .child(userId)
                       .setValue(listFriend)
                       .addOnCompleteListener(task -> {
                           if(task.isSuccessful()){
                               Toast.makeText(UserProfile.this, "New friend added successfully", Toast.LENGTH_SHORT).show();
                               add.setVisibility(View.GONE);
                               message.setVisibility(View.VISIBLE);
                           }
                       });

    }

    private void chatProfile() {

        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkOnlineStatus("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        checkOnlineStatus("offline");
    }
}
