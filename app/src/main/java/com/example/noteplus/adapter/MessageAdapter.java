package com.example.noteplus.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.noteplus.Model.Chat;
import com.example.noteplus.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private Context context;
    private List<Chat> mChat;
    private String imgURL;
    FirebaseUser fuser;
    public static final int MST_TYPE_LEFT=0;
    public static final int MSG_TYPE_RIGHT=1;
    public MessageAdapter(Context context, List<Chat> mChat,String imgURL) {
        this.context = context;
        this.mChat = mChat;
        this.imgURL=imgURL;
    }
    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_right,
                    parent,
                    false);

            return new MessageAdapter.ViewHolder(view);
        }else{
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_left,
                    parent,
                    false);

            return new MessageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
       Chat chat=mChat.get(position);
       holder.show_message.setText(chat.getMessage());
       if(imgURL.equals("default")){
           holder.profile_image.setImageResource(R.mipmap.ic_launcher);
       }else{
           Glide.with(context).load(imgURL).into(holder.profile_image);
       }
       if(position==mChat.size()-1){
           if(chat.isIsseen()){
               holder.txt_seen.setText("Seen");
           }else {
               holder.txt_seen.setText("Delivered");
           }
       }else {
           holder.txt_seen.setVisibility(View.GONE);
       }
    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView show_message;
        public ImageView profile_image;
        public TextView txt_seen;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profile_image = itemView.findViewById(R.id.profile_image);
            show_message=itemView.findViewById(R.id.show_message);
            txt_seen=itemView.findViewById(R.id.txt_seen_status);
        }
    }
    @Override
    public int getItemViewType(int position){
        fuser= FirebaseAuth.getInstance().getCurrentUser();
        if(mChat.get(position).getSender().equals(fuser.getUid())){
            return  MSG_TYPE_RIGHT;
        }else {
            return MST_TYPE_LEFT;
        }
    }
}

