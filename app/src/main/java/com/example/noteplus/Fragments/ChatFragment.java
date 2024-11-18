package com.example.noteplus.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.noteplus.Model.Chat;
import com.example.noteplus.Model.ChatList;
import com.example.noteplus.Model.Users;
import com.example.noteplus.R;
import com.example.noteplus.adapter.UserAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class ChatFragment extends Fragment {

    private UserAdapter userAdapter;
    private List<Users> mUsers;
    private FirebaseUser fuser;
    private DatabaseReference reference;
    private List<ChatList> usersList;
    private RecyclerView recyclerView;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_chat,container,false);
        recyclerView=view.findViewById(R.id.chatRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        fuser=FirebaseAuth.getInstance().getCurrentUser();
        usersList=new ArrayList<>();
        Log.d("ChatFragment", "Current user ID: " + fuser.getUid());
        reference= FirebaseDatabase.getInstance().getReference("ChatList")
         .child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("ChatFragment", "data changed");
                usersList.clear();
                Log.d("ChatFragment", "Snapshot data: " + snapshot.getValue());
                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    Log.d("ChatFragment", "Chat data: " + snapshot1.getValue());
                    ChatList chatList=snapshot1.getValue(ChatList.class);
                    usersList.add(chatList);
                }
                for (ChatList chatList : usersList) {
                    Log.d("ChatFragment", "ChatList: " + chatList.getId().toString());
                }
                chatList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return view;
    }
    private  void chatList(){
        Log.d("ChatFragment", "chatList method called");
        mUsers=new ArrayList<>();
        reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers.clear();
                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    Users user=snapshot1.getValue(Users.class);
                    Log.d("ChatFragment", "User data: " + snapshot1.getValue());
                    for(ChatList chatList:usersList){
                        Log.d("ChatFragment", "ChatList data: " + chatList.getId());
                        if(user.getId().equals(chatList.getId())){
                            mUsers.add(user);
                        }
                    }
                }
                for (Users user : mUsers) {
                    Log.d("ChatFragment", "User: " + user.getUsername());
                }
                userAdapter=new UserAdapter(getContext(),mUsers,true);
                recyclerView.setAdapter(userAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}