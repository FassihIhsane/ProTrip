package com.example.protrip.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.protrip.R;


import de.hdodenhof.circleimageview.CircleImageView;

public class UserViewHolder extends RecyclerView.ViewHolder {

    public CircleImageView avatar;
    public TextView userName;
    public ImageView online, offline;
    public UserViewHolder(@NonNull View itemView) {
        super(itemView);
        avatar = itemView.findViewById(R.id.user_picture);
        userName = itemView.findViewById(R.id.name);
        online = itemView.findViewById(R.id.online);
        offline = itemView.findViewById(R.id.offline);
    }
}
