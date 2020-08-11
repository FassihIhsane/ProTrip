package com.example.protrip.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.protrip.R;
import com.example.protrip.adapters.TripViewHolder;
import com.example.protrip.data.Trip;
import com.example.protrip.util.Constant;
import com.example.protrip.util.DB;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import java.util.Objects;

public class MyTripsActivity extends AppCompatActivity  {

    private RecyclerView myTripsRV;

    private FirebaseRecyclerAdapter<Trip, TripViewHolder> firebaseRecyclerAdapter;
    private Query mQueryCurrent;

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Trip> options = new FirebaseRecyclerOptions.Builder<Trip>()
                                                                           .setQuery(mQueryCurrent, Trip.class)
                                                                           .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Trip, TripViewHolder>(options) {
            @NonNull
            @Override
            public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                return new TripViewHolder(
                        LayoutInflater.from(parent.getContext())
                                      .inflate(R.layout.my_trip_row, parent, false)
                );
            }

            @Override
            protected void onBindViewHolder(@NonNull TripViewHolder entryViewHolder, int position, @NonNull Trip data) {

                entryViewHolder.destination.setText(data.getDestination());
                entryViewHolder.date.setText(data.getDate());
                entryViewHolder.delete.setOnClickListener(v ->
                        DB.getReference(Constant.TRIPS)
                          .child(data.getId())
                          .removeValue()
                          .addOnSuccessListener(aVoid ->
                                  Toast.makeText(MyTripsActivity.this, "Trip deleted successfully!", Toast.LENGTH_SHORT).show())
                          );
            }
        };

        myTripsRV.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_trips);
        setTitle(Constant.TRIPS);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        initViews();
        initFirebase();
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseRecyclerAdapter.stopListening(); // To avoid memory leaks
    }

    private void initFirebase() {

        DatabaseReference mDatabase = DB.getReference(Constant.TRIPS);
        mQueryCurrent = mDatabase.orderByChild("userId")
                                 .equalTo(DB.getUserId());
        mDatabase.keepSynced(true);
    }

    private void initViews() {

        myTripsRV = findViewById(R.id.my_trips);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        myTripsRV.setLayoutManager(layoutManager);
        myTripsRV.setHasFixedSize(true);

    }

}