package com.example.protrip.adapters;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.protrip.R;

public class TripViewHolder extends RecyclerView.ViewHolder {

    public TextView destination, date;
    public ImageButton delete;

    public TripViewHolder(@NonNull View itemView) {
        super(itemView);

        destination = itemView.findViewById(R.id.destination);
        date = itemView.findViewById(R.id.date);
        delete = itemView.findViewById(R.id.delete);
    }
}
