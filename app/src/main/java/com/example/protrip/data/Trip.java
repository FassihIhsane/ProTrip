package com.example.protrip.data;

import androidx.annotation.NonNull;

import com.example.protrip.util.Constant;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.Exclude;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Trip {

    private String id, destination, userId, date;
    private double longitude, latitude;

    public Trip() {
    }

    public Trip(String destination, String userId, double longitude, double latitude) {
        this.destination = destination;
        this.userId = userId;
        this.longitude = longitude;
        this.latitude = latitude;
        this.date = new SimpleDateFormat(Constant.DATE_FORMAT, Locale.getDefault()).format(new Date());

    }

    @NonNull
    @Override
    public String toString() {
        return this.destination + " | "+this.userId+" | "+this.id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Exclude
    public LatLng getLatLng(){

        return new LatLng(this.latitude, this.longitude);
    }
}
