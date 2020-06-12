package com.example.protrip.ui;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.lib.widget.snackbar.Snackbar;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.protrip.R;
import com.example.protrip.data.Trip;
import com.example.protrip.util.Constant;
import com.example.protrip.util.DB;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener, DialogInterface.OnDismissListener {

    private static final String TAG = "MapsActivityDebug";
    private GoogleMap mMap;
    private Marker mMarker;
    private Dialog markerDialog;
    private TextView destination;
    private ProgressBar crudProgress;
    private ImageButton add , delete;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        //if(mapFragment != null)
        mapFragment.getMapAsync(this);

        initUI();
    }

    private void initUI() {

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.auth_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.profil:
                showProfil();
                return true;
            case R.id.logout:
                logout();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void logout() {
        mAuth.signOut();
        startActivity(new Intent(this,FirstLunchActivity.class));
        finish();
    }

    private void showProfil() {

        startActivity(new Intent(this,ProfileActivity.class));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        /* Add a marker in Sydney and move the camera | Latitude & Longitude
        LatLng sydney = new LatLng(-34, 151);
        MarkerOptions sydnyMarker = new MarkerOptions().position(sydney)
                                                       .title("Marker in Sydney");
        mMap.addMarker(sydnyMarker);

        // Move camera view to sydny
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        */

        setUpMarker();
        initTrips();
    }

    private void initTrips() {

        DB.getReference(Constant.TRIPS)
          .addValueEventListener(new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                  GenericTypeIndicator<HashMap<String, Trip>> genericTypeIndicator  = new GenericTypeIndicator<HashMap<String, Trip>>() {};
                  HashMap<String, Trip> trips = dataSnapshot.getValue(genericTypeIndicator);

                  if(trips != null){

                      for(Trip t : trips.values()){

                          mMap.addMarker(
                                  new MarkerOptions().position(t.getLatLng())
                                          .title(t.getDestination())
                          );
                      }
                  }

              }

              @Override
              public void onCancelled(@NonNull DatabaseError databaseError) {

              }
          });
    }

    private void setUpMarker() {

        mMap.setOnMapClickListener(latLng -> {

            // Add marker to map
            MarkerOptions newMarkerOpt = new MarkerOptions().position(latLng)
                                                            .title("");

            Marker newMarker = mMap.addMarker(newMarkerOpt);
            setUpMarkerDialog(newMarker); // Ihsane liked it

        });

        mMap.setOnMarkerClickListener(marker -> {

            setUpMarkerDialog(marker);
            Log.d(TAG, "onMarkerClick: "+marker.getTitle());

            return false;
        });
    }

    private void setUpMarkerDialog(Marker marker) {

        markerDialog = new Dialog(this);
        markerDialog.setContentView(R.layout.marker_dialog);
        destination =markerDialog.findViewById(R.id.destination);
         add = markerDialog.findViewById(R.id.add);
         delete = markerDialog.findViewById(R.id.delete);
        ImageButton  message = markerDialog.findViewById(R.id.message);
        crudProgress = markerDialog.findViewById(R.id.crud_progress);
        mMarker = marker;
        add.setOnClickListener(this);
        delete.setOnClickListener(this);
        message.setOnClickListener(this);
        markerDialog.setOnDismissListener(this);
        markerDialog.show();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.add :
                addTrip();
                break;
            case R.id.delete :
                deleteTrip();
                break;
            case R.id.message :
                sendMessage();
        }
    }

    private void sendMessage() {
    }

    private void deleteTrip() {
    }

    private void addTrip() {

        final String _destination = destination.getText().toString();
        if(!TextUtils.isEmpty(_destination)){
            toggleProgress(add);
            LatLng ltLn =mMarker.getPosition();
            Trip trip = new Trip(_destination, mUser.getUid(), ltLn.longitude, ltLn.latitude);
            String id =DB.getKey(Constant.TRIPS);
            trip.setId(id);
            DB.getReference(Constant.TRIPS)
              .child(id)
              .setValue(trip)
              .addOnCompleteListener(task -> {

                  if(task.isSuccessful()){
                      toggleProgress(add);
                      markerDialog.dismiss();
                      Snackbar.with(MapsActivity.this)
                              .setText("Trip inserted successfully")
                              .setDuration(Snackbar.LENGTH_SHORT)
                              .show();
                  }
              });
        }

    }

    @Override
    public void onDismiss(DialogInterface dialog) {

        final String _destination = destination.getText().toString();
        if(TextUtils.isEmpty(_destination)){
            mMarker.remove();
        }
    }
    private void toggleProgress(View v) {

        v.setVisibility(
                (v.getVisibility() == View.VISIBLE) ? View.GONE : View.VISIBLE
        );
        crudProgress.setVisibility(
                (crudProgress.getVisibility() == View.VISIBLE) ? View.GONE : View.VISIBLE
        );
    }
}