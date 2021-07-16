package com.example.tinder.Cards;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.tinder.Cards.Cards;
import com.example.tinder.R;

import java.util.List;

public class arrayAdapter extends ArrayAdapter<Cards> {
    Context context;


    public arrayAdapter(@NonNull Context context,int resource,List<Cards> items) {
        super(context,resource,items);
    }

    @NonNull
    @Override
    public View getView(int position,@Nullable View convertView,@NonNull ViewGroup parent) {
        Cards card_item=getItem(position);
        if(convertView==null)
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.item,parent,false);
        TextView name=convertView.findViewById(R.id.name);
        ImageView image=convertView.findViewById(R.id.image);
        name.setText(card_item.getName());
        switch (card_item.getProfileImageUrl()){
            case "default":
                Glide.with(convertView.getContext()).load(R.drawable.ic_setting).into(image);
                break;
                default:
                    Glide.clear(image);
                    Glide.with(convertView.getContext()).load(card_item.getProfileImageUrl()).into(image);
                    break;
        }

        return convertView;
    }
}
