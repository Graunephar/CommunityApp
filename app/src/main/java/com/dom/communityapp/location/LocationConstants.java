package com.dom.communityapp.location;

import com.google.android.gms.location.LocationRequest;

import static com.google.android.gms.location.LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;
import static com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY;

/**
 * Created by daniel on 11/30/17.
 */

public class LocationConstants {

    public static final LocationRequest LOCATION_HIGH = new LocationRequest()
            .setPriority(PRIORITY_HIGH_ACCURACY)
            .setInterval(100);

    public static final LocationRequest LOCATION_LOW = new LocationRequest()
            .setPriority(PRIORITY_BALANCED_POWER_ACCURACY)
            .setInterval(35);

    public static final String EXTRA_LAST_LOCATION = "extra_last_location";

}
