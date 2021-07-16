package com.example.tinder.Matches;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tinder.Chat.ChatActivity;
import com.example.tinder.R;

public class MatchesViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView mMatchId,mMatchName;
    public ImageView mMatchImage;



    public MatchesViewHolders(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        mMatchId=itemView.findViewById(R.id.Matchid);
        mMatchName=itemView.findViewById(R.id.MatchName);
        mMatchImage=itemView.findViewById(R.id.MatchImage);
    }

    @Override
    public void onClick(View view) {
        Intent intent=new Intent(view.getContext(),ChatActivity.class);
        Bundle b=new Bundle();
        b.putString("matchId",mMatchId.getText().toString());
        intent.putExtras(b);
        view.getContext().startActivity(intent);


    }
}
