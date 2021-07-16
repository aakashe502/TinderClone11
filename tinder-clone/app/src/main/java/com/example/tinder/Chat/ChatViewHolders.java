package com.example.tinder.Chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tinder.R;

public class ChatViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView smessage;
        public LinearLayout mContainer;


    public ChatViewHolders(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        smessage=itemView.findViewById(R.id.message1);
        mContainer=itemView.findViewById(R.id.container);

    }

    @Override
    public void onClick(View view) {


    }
}