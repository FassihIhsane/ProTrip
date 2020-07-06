package com.example.protrip.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ConversationActivity extends AppCompatActivity {

    private RecyclerView conversationRV;
    private FirebaseRecyclerAdapter<Conversation, ConversationViewHolder> firebaseRecyclerAdapter;
    private Query mQueryCurrent;

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

                String date = new SimpleDateFormat(Constant.DATE_FORMAT, Locale.getDefault()).format(new Date(conversation.getDate()));
                conversationViewHolder.lastMsg.setText(conversation.getLastMessage());
                conversationViewHolder.date.setText(date);
                conversationViewHolder.name.setText(conversation.getName());

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

        initUI();
        initFirebase();
    }

    private void initUI() {

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