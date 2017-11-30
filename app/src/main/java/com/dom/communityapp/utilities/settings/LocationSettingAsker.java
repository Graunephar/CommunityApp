package com.dom.communityapp.utilities.settings;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.dom.communityapp.MapsActivity;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import static com.google.android.gms.location.LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;
import static com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY;

/**
 * Created by daniel on 11/30/17.
 */

public class LocationSettingAsker implements SettingAsker{

    private static final int REQUEST_CHECK_SETTINGS = 42;
    private final Activity context;

    private LocationRequest high = new LocationRequest()
            .setPriority(PRIORITY_HIGH_ACCURACY)
            .setInterval(10);

    private LocationRequest low = new LocationRequest()
            .setPriority(PRIORITY_BALANCED_POWER_ACCURACY)
            .setInterval(35);

    public LocationSettingAsker(Activity context) {
        this.context = context;
    }

    @Override
    public void ask() {


        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(high)
                .addLocationRequest(low);


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



}
