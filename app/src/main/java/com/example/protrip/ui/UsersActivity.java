package com.example.protrip.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.protrip.R;
import com.example.protrip.adapters.ConversationViewHolder;
import com.example.protrip.adapters.UserViewHolder;
import com.example.protrip.data.Conversation;
import com.example.protrip.data.ListFriend;
import com.example.protrip.data.User;
import com.example.protrip.notifications.Data;
import com.example.protrip.util.Constant;
import com.example.protrip.util.DB;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class UsersActivity extends AppCompatActivity {

    private RecyclerView userRV;
    private FirebaseRecyclerAdapter<ListFriend, UserViewHolder> firebaseRecyclerAdapter;
    private FirebaseRecyclerAdapter<User, UserViewHolder> firebaseRecyclerAdapterUser;
    private Query mQueryCurrent;
    private StorageReference mStorageRef;
    DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        initUI();
        initFirebase();
    }

    private void initFirebase() {
        DatabaseReference mDatabase = DB.getReference(Constant.FRIENDS);
        mQueryCurrent = mDatabase.child(DB.getUserId());
        userRef = DB.getReference(Constant.USERS);
        mDatabase.keepSynced(true);

}

    @Override
    protected void onStart() {
        super.onStart();


        FirebaseRecyclerOptions<ListFriend> options = new FirebaseRecyclerOptions.Builder<ListFriend>()
                .setQuery(mQueryCurrent, ListFriend.class)
                .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<ListFriend, UserViewHolder>(options){

            @NonNull
            @Override
            public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                return new UserViewHolder(
                        LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.user_row, parent, false)
                );
            }

            @Override
            protected void onBindViewHolder(@NonNull UserViewHolder userViewHolder, int i, @NonNull ListFriend listFriend) {

                String usersId = getRef(i).getKey();

                assert usersId != null;
                userRef.child(usersId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChild("fullName")){
                            String name =  snapshot.child("fullName").getValue().toString();
                            String status = snapshot.child("status").getValue().toString();

                            userViewHolder.userName.setText(name);
                            if(status.equals("online")){
                                userViewHolder.online.setVisibility(View.VISIBLE);
                                userViewHolder.offline.setVisibility(View.GONE);
                            }else{
                                userViewHolder.online.setVisibility(View.GONE);
                                userViewHolder.offline.setVisibility(View.VISIBLE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                mStorageRef = FirebaseStorage.getInstance().getReference();
                StorageReference storageReference = mStorageRef.child("users/"+listFriend.getUserId()+"/profile.jpg");
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(userViewHolder.avatar);
                    }
                });

                userViewHolder.itemView.setOnClickListener(v->{


                    Intent intent = new Intent(UsersActivity.this, UserProfile.class);
                    intent.putExtra(Constant.USERID_INTENT,listFriend.getUserId());
                    startActivity(intent);
                });
                userViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        showDialogBlock(listFriend.getUserId());
                        return true;
                    }
                });
            }
        };

        userRV.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

        }

    private void showDialogBlock(String userId) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                UsersActivity.this);

        alertDialog.setTitle("Confirm Delete...");
        alertDialog.setMessage("Are you sure you want delete this user from your list of friends ?");
// Setting Positive "Delete" Btn
        alertDialog.setPositiveButton("Delete",
                (dialog, which) -> {
                    DB.getReference(Constant.FRIENDS).child(DB.getUserId())
                            .child(userId)
                            .removeValue()
                            .addOnCompleteListener(task -> {
                                if(task.isSuccessful()){
                                    Toast.makeText(getApplicationContext(),"User deleted successfully", Toast.LENGTH_SHORT)
                                            .show();
                                }
                            });
                });
// Setting Negative "Cancel" Btn
        alertDialog.setNegativeButton("Cancel",
                (dialog, which) -> {
                    // Write your code here to execute after dialog
                    Toast.makeText(getApplicationContext(),
                            "Cancel", Toast.LENGTH_SHORT)
                            .show();
                    dialog.cancel();
                });

// Showing Alert Dialog
        alertDialog.show();
    }

    private void initUI() {
        userRV = findViewById(R.id.user_rc);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        userRV.setLayoutManager(layoutManager);
        userRV.setHasFixedSize(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseRecyclerAdapter.stopListening();
    }
    private void checkOnlineStatus(String status){
        DatabaseReference setChild = DB.getReference(Constant.USERS).child(DB.getUserId());
        setChild.child("status").setValue(status);
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