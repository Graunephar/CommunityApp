package com.dom.communityapp.permisssion;

import android.Manifest;
import android.app.Activity;
import android.content.IntentSender;
import android.support.annotation.Nullable;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.concurrent.LinkedBlockingQueue;

import pl.tajchert.nammu.Nammu;
import pl.tajchert.nammu.PermissionCallback;

import static com.dom.communityapp.location.LocationConstants.LOCATION_HIGH;
import static com.dom.communityapp.location.LocationConstants.LOCATION_LOW;

/**
 * Created by daniel on 11/30/17.
 */

public class LocationSettingAsker implements SettingAsker {

    private static final int REQUEST_CHECK_SETTINGS = 42;
    private final Activity context;
    private LinkedBlockingQueue<PermissionRequestCallback> mLocationRequestQueue;

    public LocationSettingAsker(Activity context) {
        this.context = context;
        this.mLocationRequestQueue = new LinkedBlockingQueue<>();

        Nammu.init(context.getApplicationContext());

    }


    //ref https://developer.android.com/training/location/change-location-settings.html
    @Override
    public void askToChangeSettings(@Nullable final PermissionCallback callback) {

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(LOCATION_LOW)
                .addLocationRequest(LOCATION_HIGH);

        Task<LocationSettingsResponse> result =
                LocationServices.getSettingsClient(context).checkLocationSettings(builder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    // All location settings are satisfied. The client can initialize location
                    // requests here.
                    if(callback != null) callback.permissionGranted();
                } catch (ApiException exception) {
                    switch (exception.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be fixed by showing the
                            // user a dialog.
                            try {
                                // Cast to a resolvable exception.
                                ResolvableApiException resolvable = (ResolvableApiException) exception;
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                resolvable.startResolutionForResult(
                                        context,
                                        REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            } catch (ClassCastException e) {
                                // Ignore, should be an impossible error.
                            }
                            if(callback != null) callback.permissionGranted();
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.
                            if(callback != null) callback.permissionRefused();
                            break;
                    }
                }
            }

        });

    }

    @Override
    public boolean havePermission() {
        if (Nammu.checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onResult(int requestCode, String[] permissions, int[] grantResults) {
        Nammu.onRequestPermissionsResult(requestCode, permissions, grantResults);
        return havePermission();
    }



    //https://developers.google.com/maps/documentation/android-api/current-place-tutorial#location-permission
    @Override
    public void askForPermission(final PermissionRequestCallback callback) {


        Nammu.askForPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION, new PermissionCallback() {

            @Override
            public void permissionGranted() {
                callback.onPermissionGranted();

                while (!mLocationRequestQueue.isEmpty()) { // If we have old permission requests we will let them know that there is now permission
                    PermissionRequestCallback oldcallback = mLocationRequestQueue.remove();
                    if(!oldcallback.expirable()) {
                        oldcallback.onPermissionGranted(); //TODO Old permission requests should poperbly expire at some point
                    }
                }
            }

            @Override
            public void permissionRefused() {
                callback.onPermissionRefused();
                mLocationRequestQueue.add(callback);
            }
        });

    }



}
