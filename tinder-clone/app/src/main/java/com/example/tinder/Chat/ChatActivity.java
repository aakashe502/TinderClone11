package com.example.tinder.Chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tinder.Matches.MatchesActivity;
import com.example.tinder.Matches.MatchesAdapter;
import com.example.tinder.Matches.MatchesObject;
import com.example.tinder.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mChatAdapter;
    private RecyclerView.LayoutManager mChatLayoutManager;
    private String currentUserId,matchId,chatId;
    private FirebaseUser mAuth;

    private EditText mSendEditText;
    private Button mSendButton;
    DatabaseReference mDatabaseUser;
    DatabaseReference mDatabaseChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        matchId=getIntent().getExtras().getString("matchId");

        mAuth= FirebaseAuth.getInstance().getCurrentUser();
        currentUserId= mAuth.getUid();

        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId).child("Connections").child("Matches").child(matchId).child("chatId");
        mDatabaseChat = FirebaseDatabase.getInstance().getReference().child("Chat");
        getChatId();


        recyclerView=findViewById(R.id.recyclerView1);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(false);

        mChatLayoutManager = new LinearLayoutManager(ChatActivity.this);
        recyclerView.setLayoutManager(mChatLayoutManager);
        mChatAdapter = new ChatAdapter(getDataSetChat(),ChatActivity.this);
        recyclerView.setAdapter(mChatAdapter);

        mSendEditText=findViewById(R.id.message);
        mSendButton=findViewById(R.id.send);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
    }
    private void getChatId(){
        mDatabaseUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    chatId = dataSnapshot.getValue().toString();
                    mDatabaseChat = mDatabaseChat.child(chatId);
                    getChatMesssages();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ChatActivity.this,"user doesnt exists",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getChatMesssages() {
        mDatabaseChat.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot,@Nullable String s) {
                if(dataSnapshot.exists()){
                    String message = "";
                    String createdBuUser="";
                    if(dataSnapshot.child("text").getValue()!=null){
                        message=dataSnapshot.child("text").getValue().toString();
                    }
                    if(dataSnapshot.child("createdByUser").getValue()!=null){
                        createdBuUser=dataSnapshot.child("createdByUser").getValue().toString();
                    }
                    if(message!=null&&createdBuUser!=null){
                        boolean currentUserBoolean=false;
                        Toast.makeText(ChatActivity.this,"its running",Toast.LENGTH_SHORT).show();
                        if(createdBuUser.equals(currentUserId)){
                            currentUserBoolean=true;
                        }
                        ChatObject newMessage  = new ChatObject(message,currentUserBoolean);
                        resultChat.add(newMessage);
                        mChatAdapter.notifyDataSetChanged();

                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot,@Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot,@Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void sendMessage() {
        String sendMessageText = mSendEditText.getText().toString();
        if(!sendMessageText.isEmpty()){

            DatabaseReference newMessageDb=mDatabaseChat.push();

            Map newMessage = new HashMap();
            newMessage.put("createdByUser", currentUserId);
            newMessage.put("text", sendMessageText);

            newMessageDb.setValue(newMessage);

        }
        mSendEditText.setText(null);

    }

    private ArrayList<ChatObject> resultChat=new ArrayList<ChatObject>();

    private List<ChatObject> getDataSetChat() {
        return resultChat;
    }
}
