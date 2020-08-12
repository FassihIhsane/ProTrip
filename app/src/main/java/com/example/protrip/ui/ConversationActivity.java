package com.example.protrip.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.example.protrip.R;
import com.example.protrip.adapters.ConversationViewHolder;
import com.example.protrip.adapters.MessageViewHolder;
import com.example.protrip.data.Conversation;
import com.example.protrip.data.Message;
import com.example.protrip.util.Constant;
import com.example.protrip.util.DB;
import com.firebase.ui.common.ChangeEventType;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class ConversationActivity extends AppCompatActivity {

    private RecyclerView conversationRV;
    private FirebaseRecyclerAdapter<Conversation, ConversationViewHolder> firebaseRecyclerAdapter;
    private Query mQueryCurrent;
    private EditText searchCvr;

    private StorageReference mStorageRef;

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Conversation> options = new FirebaseRecyclerOptions.Builder<Conversation>()
                .setQuery(mQueryCurrent, Conversation.class)
                .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Conversation, ConversationViewHolder>(options){

            @NonNull
            @Override
            public ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                return new ConversationViewHolder(
                        LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.conversation_item, parent, false)
                );
            }

            @Override
            protected void onBindViewHolder(@NonNull ConversationViewHolder conversationViewHolder, int i, @NonNull Conversation conversation) {
                conversationViewHolder.name.setText(conversation.getName());
                String date = new SimpleDateFormat(Constant.DATE_FORMAT, Locale.getDefault()).format(new Date(conversation.getDate()));
                conversationViewHolder.lastMsg.setText(conversation.getLastMessage());
                conversationViewHolder.date.setText(date);


                mStorageRef = FirebaseStorage.getInstance().getReference();
                StorageReference storageReference = mStorageRef.child("users/"+conversation.getId()+"/profile.jpg");
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(conversationViewHolder.avatar);
                    }
                });

                conversationViewHolder.itemView.setOnClickListener(v->{

                    Intent intent = new Intent(ConversationActivity.this, ChatActivity.class);
                    intent.putExtra(Constant.USERID_INTENT,conversation.getId());
                    startActivity(intent);
                });
            }
        };

        conversationRV.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        setTitle(Constant.CONVERSATION);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        initUI();
        initFirebase();
        onSearch();
    }

    private void onSearch() {
        searchCvr.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchConversation(s.toString());

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void searchConversation(String toString) {
        if(!searchCvr.getText().toString().equals("")){
            Query query = DB.getReference(Constant.CONVERSATION).child(DB.getUserId()).orderByChild("name")
                            .startAt(toString)
                            .endAt(toString + "\uf8ff");
            FirebaseRecyclerOptions<Conversation> options = new FirebaseRecyclerOptions.Builder<Conversation>()
                    .setQuery(query, Conversation.class)
                    .build();

            firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Conversation, ConversationViewHolder>(options){

                @NonNull
                @Override
                public ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                    return new ConversationViewHolder(
                            LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.conversation_item, parent, false)
                    );
                }

                @Override
                protected void onBindViewHolder(@NonNull ConversationViewHolder conversationViewHolder, int i, @NonNull Conversation conversation) {
                    conversationViewHolder.name.setText(conversation.getName());
                    String date = new SimpleDateFormat(Constant.DATE_FORMAT, Locale.getDefault()).format(new Date(conversation.getDate()));
                    conversationViewHolder.lastMsg.setText(conversation.getLastMessage());
                    conversationViewHolder.date.setText(date);


                    mStorageRef = FirebaseStorage.getInstance().getReference();
                    StorageReference storageReference = mStorageRef.child("users/"+conversation.getId()+"/profile.jpg");
                    storageReference.getDownloadUrl().addOnSuccessListener(uri -> Picasso.get().load(uri).into(conversationViewHolder.avatar));

                    conversationViewHolder.itemView.setOnClickListener(v->{

                        Intent intent = new Intent(ConversationActivity.this, ChatActivity.class);
                        intent.putExtra(Constant.USERID_INTENT,conversation.getId());
                        startActivity(intent);
                    });
                }
            };

            conversationRV.setAdapter(firebaseRecyclerAdapter);
            firebaseRecyclerAdapter.startListening();
        }else{
            onStart();
        }
    }

    private void initUI() {

        searchCvr = findViewById(R.id.search_cvr);
        conversationRV = findViewById(R.id.conversation_rc);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        conversationRV.setLayoutManager(layoutManager);
        conversationRV.setHasFixedSize(true);

    }
    private void initFirebase() {

        DatabaseReference mDatabase = DB.getReference(Constant.CONVERSATION);
        mQueryCurrent = mDatabase.child(DB.getUserId())
                                 .orderByKey();
        mDatabase.keepSynced(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseRecyclerAdapter.stopListening(); // To avoid memory leaks
    }


}