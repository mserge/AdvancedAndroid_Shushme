package com.example.android.shushme;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mserge on 23.03.2018.
 */

public class Geofencing implements ResultCallback<Status> {
    private static final long GEOFENCE_TIMEOUT_MILLIS = 60 * 60 * 24 * 3 * 1000;
    private static final float GEOFENCE_RADIUS_MTR = 50;
    private static final String TAG = Geofencing.class.getSimpleName();
    //  (1) Create a Geofencing class with a Context and GoogleApiClient constructor that
    // initializes a private member ArrayList of Geofences called mGeofenceList
    private Context mContext;
    private GoogleApiClient mGoogleApiClient;
    private List<Geofence> mGeofenceList;
    private PendingIntent mPendingIntent;

    public Geofencing(Context mContext, GoogleApiClient mGoogleApiClient) {
        this.mContext = mContext;
        this.mGoogleApiClient = mGoogleApiClient;
        mGeofenceList = new ArrayList<>();
        mPendingIntent = null;
    }

    //  (2) Inside Geofencing, implement a public method called updateGeofencesList that
    // given a PlaceBuffer will create a Geofence object for each Place using Geofence.Builder
    // and add that Geofence to mGeofenceList
    public void updateGeofencesList(PlaceBuffer buffer) {
        mGeofenceList = new ArrayList<>();
        if (buffer != null && buffer.getCount() > 0) {

            for (Place place : buffer) {
                String id = place.getId();
                double latitude = place.getLatLng().latitude;
                double longitude = place.getLatLng().longitude;

                mGeofenceList.add(new Geofence.Builder()
                        .setRequestId(id)
                        .setExpirationDuration(GEOFENCE_TIMEOUT_MILLIS)
                        .setCircularRegion(latitude, longitude, GEOFENCE_RADIUS_MTR)
                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                        .build());
            }
        }
    }

    //  (3) Inside Geofencing, implement a private helper method called getGeofencingRequest that
    // uses GeofencingRequest.Builder to return a GeofencingRequest object from the Geofence list
    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        return   builder
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofences(mGeofenceList)
                .build();
    }

    //  (5) Inside Geofencing, implement a private helper method called getGeofencePendingIntent that
    // returns a PendingIntent for the GeofenceBroadcastReceiver class
    private PendingIntent getGeofencePendingIntent(){
        if(mPendingIntent!=null)
            return mPendingIntent;

        Intent intent = new Intent(mContext, GeofenceBroadcastReceiver.class);
        mPendingIntent = PendingIntent.getBroadcast(mContext, 0 , intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return mPendingIntent;
    }

    //  (6) Inside Geofencing, implement a public method called registerAllGeofences that
    //  the GeofencingRequest by calling LocationServices.GeofencingApi.addGeofences
    // using the helper functions getGeofencingRequest() and getGeofencePendingIntent()
    public void registerAllGeofences(){
        if(mGoogleApiClient == null || !mGoogleApiClient.isConnected()
                ||mGeofenceList == null || mGeofenceList.size() == 0)
            return; // not all req met

        try {
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    getGeofencingRequest(),
                    getGeofencePendingIntent()
            ).setResultCallback(this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResult(@NonNull Status status) {
        Log.e(TAG, "onResult status: " + status.getStatus().toString());
    }
    //  (7) Inside Geofencing, implement a public method called unRegisterAllGeofences that
    // unregisters all geofences by calling LocationServices.GeofencingApi.removeGeofences
    // using the helper function getGeofencePendingIntent()
    public void unRegisterAllGeofences(){
        if(mGoogleApiClient == null || !mGoogleApiClient.isConnected())
            return; // not all req met

        try {
            LocationServices.GeofencingApi.removeGeofences(
                    mGoogleApiClient,
                    getGeofencePendingIntent()
            ).setResultCallback(this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

}
