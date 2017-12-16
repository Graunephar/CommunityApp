package com.dom.communityapp.location;

import android.content.Context;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by daniel on 12/10/17.
 */


public interface LocationListener {

    void locationIncoming(Location location);
}
