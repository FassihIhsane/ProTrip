package com.example.protrip.adapters;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.protrip.R;


public class ConversationViewHolder extends RecyclerView.ViewHolder {

    public TextView name, lastMsg, date;


    public ConversationViewHolder(@NonNull View itemView) {
        super(itemView);

        name = itemView.findViewById(R.id.conversation_name);
        lastMsg = itemView.findViewById(R.id.last_msg);
        date = itemView.findViewById(R.id.date);
    }
}
