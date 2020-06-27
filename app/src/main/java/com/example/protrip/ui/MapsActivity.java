package com.example.protrip.ui;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.lib.widget.snackbar.Snackbar;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener, DialogInterface.OnDismissListener {

    private static final String TAG = "MapsActivityDebug";
    private GoogleMap mMap;
    private Marker mMarker;
    private Dialog markerDialog;
    private TextView destination;
    private ProgressBar crudProgress;
    private ImageButton add , delete, message, update;
    private Trip mTrip;
    private HashMap<String, Marker> fetchedTrips;

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
                launchActivity(ProfileActivity.class);
                return true;
            case R.id.logout:
                logout();
                return true;
            case R.id.my_trips:
                launchActivity(MyTripsActivity.class);
                return true;
            case R.id.conversation:
                launchActivity(ConversationActivity.class);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void launchActivity(Class<?> activity){

        startActivity(new Intent(this, activity));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        setUpMarker();
        initTrips();
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
                break;

            case R.id.update:
                updateTrip();
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {

        // Masterpiece handles every case
        if(mTrip == null){

            mMarker.remove();
        }
    }

    private void logout() {
        mAuth.signOut();
        startActivity(new Intent(this,FirstLunchActivity.class));
        finish();
    }

    private void initTrips() {

        SimpleDateFormat sdf = new SimpleDateFormat(Constant.DATE_FORMAT, Locale.getDefault());
        Calendar today = Calendar.getInstance();
        today.add(Calendar.DAY_OF_YEAR, -1);
        today.set(Calendar.HOUR_OF_DAY, 23);
        today.set(Calendar.MINUTE, 59);

        final String startDate = sdf.format(today.getTime());

        fetchedTrips = new HashMap<>();

        DB.getReference(Constant.TRIPS)
          .orderByChild("date")
          .startAt(startDate)
          .addChildEventListener(new ChildEventListener() {
              @Override
              public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                  Trip t = dataSnapshot.getValue(Trip.class);
                  if(t != null){

                      addMarkerToMap(t);
                  }
              }

              @Override
              public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

              }

              @Override
              public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                  Trip t = dataSnapshot.getValue(Trip.class);
                  if(t != null){

                      Objects.requireNonNull(fetchedTrips.get(t.getId()))
                             .remove();
                  }
              }

              @Override
              public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

              }

              @Override
              public void onCancelled(@NonNull DatabaseError databaseError) {

              }
          });
    }

    private void addMarkerToMap(Trip t) {

        Marker addedMarker = mMap.addMarker(
                new MarkerOptions().position(t.getLatLng())
                        .title(t.getDestination())
        );

        addedMarker.setTag(t);
        fetchedTrips.put(t.getId(), addedMarker);
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

            return false;
        });
    }

    private void setUpMarkerDialog(Marker marker) {

        final Trip trip =  (marker.getTag() != null) ? (Trip) marker.getTag() : null;

        markerDialog = new Dialog(this);
        markerDialog.setContentView(R.layout.marker_dialog);
        destination =markerDialog.findViewById(R.id.destination);
        add = markerDialog.findViewById(R.id.add);
        delete = markerDialog.findViewById(R.id.delete);
        message = markerDialog.findViewById(R.id.message);
        update = markerDialog.findViewById(R.id.update);
        crudProgress = markerDialog.findViewById(R.id.crud_progress);
        mMarker = marker;

        mTrip = trip;
        secureMarkerOperations(trip);

        add.setOnClickListener(this);
        delete.setOnClickListener(this);
        message.setOnClickListener(this);
        update.setOnClickListener(this);
        markerDialog.setOnDismissListener(this);
        destination.setText(marker.getTitle());     // Set destination of the trip to EditText

        markerDialog.show();
    }

    private void secureMarkerOperations(Trip trip) {

        if(trip != null){

            // If it wasn't created by the logged user
            if(!trip.getUserId().equals(mUser.getUid())){

                delete.setVisibility(View.GONE);
                add.setVisibility(View.GONE);
                update.setVisibility(View.GONE);
                message.setVisibility(View.VISIBLE);
                destination.setEnabled(false);
                message.setLayoutParams(getSingleParam(4.0f));

            }else{

                add.setVisibility(View.GONE);
                update.setVisibility(View.VISIBLE);
                message.setVisibility(View.GONE);

                LinearLayout.LayoutParams param = getSingleParam(2.0f);
                delete.setLayoutParams(param);
                update.setLayoutParams(param);
            }
        }
    }

    private void updateTrip() {

        if(mTrip != null){

            final String newDestination = destination.getText().toString();
            mTrip.setDestination(
                    TextUtils.isEmpty(newDestination) ? mTrip.getDestination() : newDestination
            );

            DB.getReference(Constant.TRIPS)
              .child(mTrip.getId())
              .setValue(mTrip)
              .addOnCompleteListener(task ->{

                  if(task.isSuccessful()){

                      showSnackBar("Trip was updated successfully!");
                      markerDialog.dismiss();

                      // Holy fuck this was very challenging | it took about 2hours :V
                      mMarker.remove();
                      fetchedTrips.get(mTrip.getId())
                                  .remove();
                      addMarkerToMap(mTrip);
                  }
              });
        }

    }

    private void sendMessage() {

        // TODO send message
        String userId = mTrip.getUserId();
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
    }

    private void deleteTrip() {

        if(mTrip != null){

            DB.getReference(Constant.TRIPS)
              .child(mTrip.getId())
              .removeValue()
              .addOnCompleteListener(task ->{

                if(task.isSuccessful()){

                    showSnackBar("Trip was deleted successfully!");
                    markerDialog.dismiss();
                    fetchedTrips.get(mTrip.getId())
                                .remove();
                }
            });
        }
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
                            showSnackBar("Trip was added successfully");
                        }
                    });
        }

    }

    private void showSnackBar(String msg) {

        Snackbar.with(MapsActivity.this)
                .setText(msg)
                .setDuration(Snackbar.LENGTH_SHORT)
                .show();
    }

    private void toggleProgress(View v) {

        v.setVisibility(
                (v.getVisibility() == View.VISIBLE) ? View.GONE : View.VISIBLE
        );
        crudProgress.setVisibility(
                (crudProgress.getVisibility() == View.VISIBLE) ? View.GONE : View.VISIBLE
        );
    }

    private LinearLayout.LayoutParams getSingleParam(float weight){

        return new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
                weight
        );
    }
}