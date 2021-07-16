package com.example.tinder.Chat;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tinder.R;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatViewHolders> {
    private List<ChatObject> ChatList;
    private Context context;

    public ChatAdapter(List<ChatObject> ChatList,Context context) {
        this.ChatList = ChatList;
        this.context = context;
    }

    @NonNull
    @Override
    public ChatViewHolders onCreateViewHolder(@NonNull ViewGroup parent,int viewType) {
        View layoutview= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chats,null,false);
        RecyclerView.LayoutParams ip=new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutview.setLayoutParams(ip);
        ChatViewHolders rev=new ChatViewHolders(layoutview);
        return rev;
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolders holder,int position) {
        holder.smessage.setText(ChatList.get(position).getMessage());
        if(ChatList.get(position).isCurrentUser()==true){

            holder.smessage.setGravity(Gravity.END);
            holder.smessage.setTextColor(Color.parseColor("#404040"));
            holder.mContainer.setBackgroundColor(Color.parseColor("#F4F4F4"));
        }
        else{
            holder.smessage.setGravity(Gravity.START);
            holder.smessage.setTextColor(Color.parseColor("#FFFFFF"));
            holder.mContainer.setBackgroundColor(Color.parseColor("#2DB4CB"));
        }

    }

    @Override
    public int getItemCount() {
        return ChatList.size();
    }
}
