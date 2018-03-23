package com.example.android.shushme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by mserge on 23.03.2018.
 */

//  (4) Create a GeofenceBroadcastReceiver class that extends BroadcastReceiver and override
// onReceive() to simply log a message when called. Don't forget to add a receiver tag in the Manifest

public class GeofenceBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "GeofenceBR";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive" + intent.getAction());
    }
}
