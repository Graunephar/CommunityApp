package com.dom.communityapp.location;

import android.location.Location;

/**
 * Created by daniel on 12/10/17.
 */

public interface LocationUpdateCallback {
    void newLocation(Location location);

    void failed(Exception exception);
}
