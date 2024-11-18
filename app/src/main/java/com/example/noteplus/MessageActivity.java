package com.example.noteplus;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.noteplus.Model.Chat;
import com.example.noteplus.Model.Users;
import com.example.noteplus.adapter.MessageAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MessageActivity extends AppCompatActivity {
    private TextView username;
    private ImageView imageView;
    private FirebaseUser fuser;
    private DatabaseReference reference;
    private Intent intent;
    private RecyclerView recyclerView;
    private EditText msg_editText;
    private ImageButton sendBtn;
    private MessageAdapter messageAdapter;
    private List<Chat> mChat;
    private RecyclerView recyclerView1;
    private DatabaseReference myRef;
    private ImageView msgback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        imageView=findViewById(R.id.imageview_profile);
        username=findViewById(R.id.userNameOutput);
        Toolbar toolbar=findViewById(R.id.toolbar);
        msgback=findViewById(R.id.Back_message);
        msgback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i =new Intent(MessageActivity.this, ChatActivity.class);
                startActivity(i);
                finish();
            }
        });
        recyclerView1=findViewById(R.id.recycler_view);
        recyclerView1.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView1.setLayoutManager(linearLayoutManager);


        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sendBtn=findViewById(R.id.btn_send);
        msg_editText=findViewById(R.id.text_send);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    finish();
            }
        });
    intent=getIntent();
    String userid=intent.getStringExtra("userid");
    fuser= FirebaseAuth.getInstance().getCurrentUser();
    reference= FirebaseDatabase.getInstance().getReference("Users").child(userid);
    reference.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            Log.d("Firebase", "Snapshot: " + snapshot.toString());
            Users user=snapshot.getValue(Users.class);
            username.setText(user.getUsername());
            if(user.getImageURL().equals("default")){
                imageView.setImageResource(R.mipmap.ic_launcher);
            }else{
                Glide.with(MessageActivity.this).load(user.getImageURL()).into(imageView);
            }
            readMessages(fuser.getUid(),userid,user.getImageURL());
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    });
    sendBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String msg=msg_editText.getText().toString();
            if(!msg.equals("")){
                sendMessage(fuser.getUid(),userid,msg);
            }else{
                Toast.makeText(MessageActivity.this,"Please Send a Nonnull messsage",Toast.LENGTH_SHORT).show();
            }
            msg_editText.setText("");
        }
    });
    }
    private void sendMessage(String sender,String receiver,String message){
        DatabaseReference reference1=FirebaseDatabase.getInstance().getReference();
        HashMap<String,Object> hashMap=new HashMap<String,Object>();
        hashMap.put("sender",sender);
        hashMap.put("receiver",receiver);
        hashMap.put("message",message);

        reference1.child("Chats").push().setValue(hashMap);
    }
    private  void readMessages(String myid,String userid,String imageurl){
        mChat = new ArrayList<>();
        reference=FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mChat.clear();
                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    Log.d("ChatSnapshot", "Chat data: " + snapshot1.getValue());
                    Chat chat=snapshot1.getValue(Chat.class);
                    Log.d("ChatSnapshot", "Chat datar: " + chat.getReceiver());
                    Log.d("ChatSnapshot", "Chat datas: " + chat.getSender());
                    if(chat.getReceiver().equals(myid)&&chat.getSender().equals(userid)||
                    chat.getReceiver().equals(userid)&&chat.getSender().equals(myid)){
                        mChat.add(chat);
                    }
                }
                messageAdapter =new MessageAdapter(MessageActivity.this,mChat,imageurl);
                recyclerView1.setAdapter(messageAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}