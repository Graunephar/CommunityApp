package com.dom.communityapp.location;

import android.app.Activity;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.concurrent.LinkedBlockingQueue;

import static com.dom.communityapp.location.LocationConstants.LOCATION_HIGH;
import static com.dom.communityapp.location.LocationConstants.LOCATION_LOW;

/**
 * Created by daniel on 11/30/17.
 */

public class LocationSettingAsker implements SettingAsker{

    private static final int REQUEST_CHECK_SETTINGS = 42;
    private final Activity context;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;
    private LinkedBlockingQueue<PermissionRequestCallback> mLocationRequestQueue;

    public LocationSettingAsker(Activity context) {
        this.context = context;
        this.mLocationRequestQueue = new LinkedBlockingQueue<>();

    }

    @Override
    public void ask() {

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
                    //TODO We have connection
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
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.
                            //TODO Should we do something here????
                            break;
                    }
                }
            }
        });

    }

    @Override
    public boolean havePermission() {
        return mLocationPermissionGranted;
    }

    @Override
    public boolean onResult(int requestCode, String[] permissions, int[] grantResults) {

        boolean result = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                    //TODO Should the quere expire? This should be probably tested.
                    //Empty request queue make everything happen
                    for (PermissionRequestCallback callback : mLocationRequestQueue) {
                        callback.onPermissionGranted();
                    }

                    result = true;
                }
            }
        }

        return result;

    }



    //https://developers.google.com/maps/documentation/android-api/current-place-tutorial#location-permission
    public void getLocationPermission(PermissionRequestCallback callback) {
    /*
     * Request location permission, so that we can get the location of the
     * device. The result of the permission request is handled by a callback,
     * onRequestPermissionsResult.
     */


        if (ContextCompat.checkSelfPermission(context,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            if (callback != null) callback.onPermissionGranted();


        } else {
            ActivityCompat.requestPermissions(context,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            if (callback != null) mLocationRequestQueue.add(callback);
        }
    }



}
