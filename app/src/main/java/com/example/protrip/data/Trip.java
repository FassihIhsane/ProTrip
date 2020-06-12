package com.example.protrip.data;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

public class Trip {

    private String id, destination, userId;
    private double longitude, latitude;
    private long date;

    public Trip() {
    }

    public Trip(String destination, String userId, double longitude, double latitude) {
        this.destination = destination;
        this.userId = userId;
        this.longitude = longitude;
        this.latitude = latitude;
        this.date = new Date().getTime();

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

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public LatLng getLatLng(){

        return new LatLng(this.latitude, this.longitude);
    }
}
