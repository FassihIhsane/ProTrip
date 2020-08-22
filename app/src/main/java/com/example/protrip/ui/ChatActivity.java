package com.example.protrip.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.example.protrip.adapters.MessageViewHolder;
import com.example.protrip.data.Conversation;
import com.example.protrip.data.Message;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.protrip.R;
import com.example.protrip.data.User;
import com.example.protrip.notifications.APIService;
import com.example.protrip.notifications.Client;
import com.example.protrip.notifications.Data;
import com.example.protrip.notifications.MyResponse;
import com.example.protrip.notifications.Sender;
import com.example.protrip.notifications.Token;
import com.example.protrip.util.Constant;
import com.example.protrip.util.DB;
import com.firebase.ui.common.ChangeEventType;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.pedromassango.doubleclick.DoubleClick;
import com.pedromassango.doubleclick.DoubleClickListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener{

    private RecyclerView messagesRV;
    private EditText inputMessage;
    private TextView receiver, status;
    private CircleImageView avatar;
    private Toolbar toolbar;
    private ImageButton back;
    private String senderId, receiverId, conversationId, myName,receiverName;
    public static final int MSG_TYPE_RIGHT = 1;
    public static final int MSG_TYPE_LEFT = 0;
    Boolean notify = false;
    APIService apiService;
    private StorageReference mStorageRef;


    private FirebaseRecyclerAdapter<Message, MessageViewHolder> firebaseRecyclerAdapter;
    private Query mQueryCurrent;

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Message> options = new FirebaseRecyclerOptions.Builder<Message>()
                .setQuery(mQueryCurrent, Message.class)
                .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Message, MessageViewHolder>(options) {

            @Override
            public void onChildChanged(@NonNull ChangeEventType type, @NonNull DataSnapshot snapshot, int newIndex, int oldIndex) {
                super.onChildChanged(type, snapshot, newIndex, oldIndex);

                messagesRV.scrollToPosition(getItemCount() - 1);
            }

            @NonNull
            @Override
            public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                if (viewType == MSG_TYPE_RIGHT) {

                    return new MessageViewHolder(
                            LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.chat_item_right, parent, false)
                    );
                } else {
                    return new MessageViewHolder(
                            LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.chat_item_left, parent, false)
                    );
                }
            }

            @Override
            protected void onBindViewHolder(@NonNull MessageViewHolder messageViewHolder, int i, @NonNull Message message) {

                messageViewHolder.messageText.setText(message.getMessage());
                String date = new SimpleDateFormat(Constant.DATE_FORMAT, Locale.getDefault()).format(new Date(message.getDate()));
                messageViewHolder.dateMessage.setText(date);
                RelativeLayout holder = messageViewHolder.messageHolder;


            }


            @Override
            public int getItemViewType(int position) {
                Message message = getItem(position);
                if (message.getSenderId().equals(DB.getUserId())) {
                    return MSG_TYPE_RIGHT;
                } else {
                    return MSG_TYPE_LEFT;
                }
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
                notify = true;

                final String message = inputMessage.getText().toString();

                if(!TextUtils.isEmpty(message)){

                    Message m = new Message(message, senderId, receiverId);
                    DB.getReference(Constant.CHAT)
                            .child(conversationId)
                            .push()
                            .setValue(m)
                            .addOnSuccessListener(aVoid -> {

                                inputMessage.setText(""); // Clear message field
                                refreshConversation(message);
                            });
                    final String msg = message;
                    final DatabaseReference reference = DB.getReference(Constant.USERS).child(DB.getUserId());
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            assert user != null;
                            if(notify) {
                                sendNotification(receiverId, user.getFullName(), msg);
                            }
                            notify = false;
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }else {
                    Toast.makeText(ChatActivity.this, "Message cannot be empty!", Toast.LENGTH_SHORT).show();
                }
            }
        }));
        inputMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                   if(s.toString().trim().length() == 0){
                       checkTypingStatus("noOne");
                   }else {
                       checkTypingStatus(receiverId );
                   }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void sendNotification(String receiver, final String userName, final String message ){
        DatabaseReference allTokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = allTokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()){
                    Token token = ds.getValue(Token.class);
                    Data data = new Data(DB.getUserId(), userName+": "+message,"New Message",receiver,R.mipmap.ic_launcher_logo);

                    Sender sender = new Sender(data, token.getToken());
                    apiService.sendNotification(sender)
                              .enqueue(new Callback<MyResponse>() {
                                  @Override
                                  public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                      Toast.makeText(ChatActivity.this,""+response.message(),Toast.LENGTH_SHORT).show();
                                  }

                                  @Override
                                  public void onFailure(Call<MyResponse> call, Throwable t) {

                                  }
                              });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void refreshConversation(String msg) {

        // Refreshing my conversation
        final Conversation conversation = new Conversation(receiverId, receiverName, msg);
        final String myId = DB.getUserId();
        DB.getReference(Constant.CONVERSATION)
          .child(myId)
          .child(receiverId)
          .setValue(conversation)
          .addOnSuccessListener(aVoid -> {

              // Refresh receiver's conversation
              conversation.setId(myId);
              conversation.setName(myName);
              DB.getReference(Constant.CONVERSATION)
                .child(receiverId)
                .child(myId)
                .setValue(conversation);

          });
    }

    private void fetchUserName(String userId) {

        // Getting receiver's name
        DB.getReference(Constant.USERS)
          .child(userId)
          .addValueEventListener(new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                  User user = dataSnapshot.getValue(User.class);
                  if(user != null){
                      receiverName = user.getFullName();
                      receiver.setText(user.getFullName());
                      if(user.getTypingTo().equals(senderId)){
                          status.setText("Typing...");
                      }else {
                          if(user.getStatus().equals("online")){
                              status.setText(user.getStatus());
                          }
                      }
                  }
                  mStorageRef = FirebaseStorage.getInstance().getReference();
                  assert user != null;
                  StorageReference storageReference = mStorageRef.child("users/"+receiverId+"/profile.jpg");
                  storageReference.getDownloadUrl().addOnSuccessListener(uri -> Picasso.get().load(uri).into(avatar));
              }

              @Override
              public void onCancelled(@NonNull DatabaseError databaseError) {}
          });

        // Getting logged user's name
        DB.getReference(Constant.USERS)
        .child(DB.getUserId())
        .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);
                if(user != null){
                    myName = user.getFullName();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    private void initUI() {

        toolbar = findViewById(R.id.toolbar_chat);
        setSupportActionBar(toolbar);
        back = findViewById(R.id.back_chat);
        back.setOnClickListener(this);
        receiver = findViewById(R.id.receiver_name);
        status = findViewById(R.id.status_chat);
        avatar = findViewById(R.id.avatar_chat);
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        messagesRV = findViewById(R.id.messages_rv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        messagesRV.setLayoutManager(layoutManager);
        messagesRV.setHasFixedSize(true);

        inputMessage = findViewById(R.id.input_message);

        senderId = DB.getUserId();
        receiverId = getIntent().getStringExtra(Constant.USERID_INTENT);
        conversationId = (senderId.compareTo(receiverId) > 0) ? senderId.concat(receiverId) : receiverId.concat(senderId);
    }

    private void checkOnlineStatus(String status){
        DatabaseReference setChild = DB.getReference(Constant.USERS).child(DB.getUserId());
        setChild.child("status").setValue(status);
    }

    private void checkTypingStatus(String typing){
        DatabaseReference setChild = DB.getReference(Constant.USERS).child(DB.getUserId());
        setChild.child("typingTo").setValue(typing);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkOnlineStatus("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        checkTypingStatus("noOne");
        checkOnlineStatus("offline");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_chat :
                startActivity(new Intent(this, ConversationActivity.class));
        }
    }
}