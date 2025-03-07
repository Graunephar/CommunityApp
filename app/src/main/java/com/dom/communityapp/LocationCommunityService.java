package com.dom.communityapp;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Binder;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.dom.communityapp.location.BroadCastSendUtility;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import static com.dom.communityapp.location.LocationConstants.LOCATION_HIGH;
import static com.dom.communityapp.location.LocationConstants.LOCATION_LOW;
import static java.lang.Thread.sleep;

/**
 * Created by daniel on 12/10/17.
 */

public class LocationCommunityService extends Service {


    private final IBinder mBinder;
    private final String logTag = "LOCATION_SERVICE";
    private BroadCastSendUtility mBroadcastUtility;

    private FusedLocationProviderClient mFusedLocationProviderClient;

    public LocationCommunityService() {
        this.mBinder = new LocalBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        this.mBroadcastUtility = new BroadCastSendUtility(this);


        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {


            final LocationCallback locationcallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    broadCastLocation(locationResult.getLastLocation());
                }
            };

            mFusedLocationProviderClient.requestLocationUpdates(LOCATION_HIGH, locationcallback, Looper.myLooper());
            mFusedLocationProviderClient.requestLocationUpdates(LOCATION_LOW, locationcallback, Looper.myLooper());
        }
    }

    private void broadCastLocation(Location location) {
        mBroadcastUtility.broadCastLocation(location);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(logTag, "Location service destroyed");
        //mPreferenceUtility.saveToSharedPreferences(mCityNameList);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(logTag, "Service bound.");
        return mBinder;
    }


    public class LocalBinder extends Binder {
        public LocationCommunityService getService() {
            // Return this instance of LocalService so clients can call public methods
            return LocationCommunityService.this;
        }
    }

    //https://developers.google.com/maps/documentation/android-api/current-place-tutorial
    public void getDeviceLocation() {

        try {
            Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
            locationResult.addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful()) {
                        // Set the map's camera position to the current location of the device.)
                        broadCastLocation(task.getResult());

                    }
                }
            });
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }

    }


}
