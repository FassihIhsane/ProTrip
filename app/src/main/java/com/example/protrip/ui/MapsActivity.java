package com.example.protrip.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.protrip.R;
import com.example.protrip.customui.MarkerDialog;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "MapsActivityDebug";
    private GoogleMap mMap;
    private ArrayList<Marker> myMarkers = new ArrayList<>();

    private FirebaseAuth mAuth;

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
        FirebaseUser user = mAuth.getCurrentUser();
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
    }

    private void setUpMarker() {

        mMap.setOnMapClickListener(latLng -> {

            // Add marker to map
            MarkerOptions newMarkerOpt = new MarkerOptions().position(latLng)
                                                            .title("Marker "+myMarkers.size());

            Marker newMarker = mMap.addMarker(newMarkerOpt);
            newMarker.setDraggable(true);

            // Add it to my markers list
            myMarkers.add(newMarker);

        });

        mMap.setOnMarkerClickListener(marker -> {

            setUpMarkerDialog(marker);

            return false;
        });
    }

    private void setUpMarkerDialog(Marker marker) {

        MarkerDialog markerDialog = new MarkerDialog(this, marker);
        markerDialog.show();
    }
}