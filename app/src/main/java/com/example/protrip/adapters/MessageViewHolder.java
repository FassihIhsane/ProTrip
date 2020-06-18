package com.example.protrip.adapters;

import android.graphics.Color;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.protrip.R;

public class MessageViewHolder extends RecyclerView.ViewHolder {

    public TextView messageText, dateMessage;
    public RelativeLayout messageHolder;

    public MessageViewHolder(@NonNull View itemView) {
        super(itemView);

        messageText = itemView.findViewById(R.id.message_text);
        dateMessage = itemView.findViewById(R.id.date_message);
        messageHolder = itemView.findViewById(R.id.message_holder);
    }
}
