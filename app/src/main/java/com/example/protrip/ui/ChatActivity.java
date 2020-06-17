package com.example.protrip.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;

import com.example.protrip.adapters.MessageViewHolder;
import com.example.protrip.data.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.protrip.R;
import com.example.protrip.data.User;
import com.example.protrip.util.Constant;
import com.example.protrip.util.DB;
import com.firebase.ui.common.ChangeEventType;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.pedromassango.doubleclick.DoubleClick;
import com.pedromassango.doubleclick.DoubleClickListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView messagesRV;
    private EditText inputMessage;
    private String senderId, receiverId, conversationId;

    private FirebaseRecyclerAdapter<Message, MessageViewHolder> firebaseRecyclerAdapter;
    private Query mQueryCurrent;

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Message> options = new FirebaseRecyclerOptions.Builder<Message>()
                                                                              .setQuery(mQueryCurrent, Message.class)
                                                                              .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Message, MessageViewHolder>(options){

            @Override
            public void onChildChanged(@NonNull ChangeEventType type, @NonNull DataSnapshot snapshot, int newIndex, int oldIndex) {
                super.onChildChanged(type, snapshot, newIndex, oldIndex);

                messagesRV.scrollToPosition(getItemCount() - 1);
            }

            @NonNull
            @Override
            public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                return new MessageViewHolder(
                        LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.message_item, parent, false)
                );
            }

            @Override
            protected void onBindViewHolder(@NonNull MessageViewHolder messageViewHolder, int i, @NonNull Message message) {

                messageViewHolder.messageText.setText(message.getMessage());
                String date = new SimpleDateFormat(Constant.DATE_FORMAT, Locale.getDefault()).format(new Date(message.getDate()));
                messageViewHolder.dateMessage.setText(date);
                RelativeLayout holder = messageViewHolder.messageHolder;
                holder.setBackgroundColor(
                        message.getSenderId().equals(senderId) ? Color.parseColor(Constant.PRIMARY_COLOR) : Color.GRAY
                );
                holder.setGravity(getMatchedGravity(senderId));
            }
        };

        messagesRV.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initUI();
        initFirebase();
        fetchUserName(receiverId);
        handleSendMessage(receiverId);
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseRecyclerAdapter.stopListening(); // To avoid memory leaks
    }

    private int getMatchedGravity(final String sender){

        return (sender.equals(senderId)) ? Gravity.END : Gravity.START;
    }

    private void initFirebase() {

        DatabaseReference mDatabase = DB.getReference(Constant.CHAT);
        mQueryCurrent = mDatabase.child(conversationId)
                                 .orderByKey();
        mDatabase.keepSynced(true);
    }

    private void handleSendMessage(String receiverId) {

        inputMessage.setOnClickListener( new DoubleClick(new DoubleClickListener() {
            @Override
            public void onSingleClick(View view) {}

            @Override
            public void onDoubleClick(View view) {

                final String message = inputMessage.getText().toString();

                if(!TextUtils.isEmpty(message)){

                    Message m = new Message(message, senderId, receiverId);
                    DB.getReference(Constant.CHAT)
                            .child(conversationId)
                            .push()
                            .setValue(m)
                            .addOnSuccessListener(aVoid -> {

                                inputMessage.setText(""); // Clear message field
                            });

                }else {
                    Toast.makeText(ChatActivity.this, "Message cannot be empty!", Toast.LENGTH_SHORT).show();
                }
            }
        }));

        /*inputMessage.setOnClickListener(v ->{

            final String message = inputMessage.getText().toString();

            if(!TextUtils.isEmpty(message)){

                Message m = new Message(message, senderId, receiverId);
                DB.getReference(Constant.CHAT)
                  .child(conversationId)
                  .push()
                  .setValue(m)
                  .addOnSuccessListener(aVoid -> {

                      inputMessage.setText(""); // Clear message field
                  });

            }else {
                Toast.makeText(this, "Message cannot be empty!", Toast.LENGTH_SHORT).show();
            }
        });*/
    }

    private void fetchUserName(String userId) {

        DB.getReference(Constant.USERS)
          .child(userId)
          .addValueEventListener(new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                  User user = dataSnapshot.getValue(User.class);
                  if(user != null){
                      setTitle(user.getFullName());
                  }else{
                      setTitle("Unknown");
                  }
              }

              @Override
              public void onCancelled(@NonNull DatabaseError databaseError) {

              }
          });
    }

    private void initUI() {

        messagesRV = findViewById(R.id.messages_rv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        messagesRV.setLayoutManager(layoutManager);
        messagesRV.setHasFixedSize(true);

        inputMessage = findViewById(R.id.input_message);

        senderId = DB.getUserId();
        receiverId = getIntent().getStringExtra("userId");
        conversationId = (senderId.compareTo(receiverId) > 0) ? senderId.concat(receiverId) : receiverId.concat(senderId);
    }
}