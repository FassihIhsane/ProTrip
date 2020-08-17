package com.example.protrip.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.protrip.R;
import com.example.protrip.adapters.TripViewHolder;
import com.example.protrip.data.Trip;
import com.example.protrip.util.Constant;
import com.example.protrip.util.DB;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MyTripsActivity extends AppCompatActivity  {

    private RecyclerView myTripsRV;
    private EditText search;

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
        onSearchTrip();
    }


    private void onSearchTrip() {
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchTrips(s.toString());

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void searchTrips(String toString) {
        if(!search.getText().toString().equals("")) {
            Query query = DB.getReference(Constant.TRIPS).orderByChild("destination")
                    .startAt(toString)
                    .endAt(toString + "\uf8ff");

            FirebaseRecyclerOptions<Trip> options = new FirebaseRecyclerOptions.Builder<Trip>()
                    .setQuery(query, Trip.class)
                    .build();
            firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Trip, TripViewHolder>(options) {
                @Override
                protected void onBindViewHolder(@NonNull TripViewHolder tripViewHolder, int i, @NonNull Trip trip) {
                    tripViewHolder.destination.setText(trip.getDestination());
                    tripViewHolder.date.setText(trip.getDate());

                }

                @NonNull
                @Override
                public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    return new TripViewHolder(
                            LayoutInflater.from(parent.getContext())
                                    .inflate(R.layout.my_trip_row, parent, false));
                }
            };
            myTripsRV.setAdapter(firebaseRecyclerAdapter);
            firebaseRecyclerAdapter.startListening();
        }else{
            onStart();
        }
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
        search = findViewById(R.id.search_trip);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        myTripsRV.setLayoutManager(layoutManager);
        myTripsRV.setHasFixedSize(true);

    }

    private void checkOnlineStatus(String status){
        DatabaseReference setChild = DB.getReference(Constant.USERS).child(DB.getUserId());
        setChild.child("status").setValue(status);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkOnlineStatus("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        checkOnlineStatus("offline");
    }

}