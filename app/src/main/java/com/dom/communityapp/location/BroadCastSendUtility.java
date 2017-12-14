package com.dom.communityapp.location;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import static com.dom.communityapp.location.LocationConstants.EXTRA_LAST_LOCATION;


/**
 * Created by daniel on 11/22/17.
 */

public class BroadCastSendUtility {

    public static final String BROADCAST_BACKGROUND_SERVICE_RESULT = "com.dom.communityapp.location.BROADCAST_BACKGROUND_SERVICE_RESULT";

    private Context mContext;

    public BroadCastSendUtility(Context context) {
        mContext = context;
    }


    //Sending local broadcasts
    public void broadCastLocation(Location location) {

        if(location == null) return;

        //Creating broadcast intent
        Intent intent = new Intent();
        intent.setAction(BROADCAST_BACKGROUND_SERVICE_RESULT);

        //Putting in the last weather info
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_LAST_LOCATION, location);
        intent.putExtras(bundle);

        //Sending the local broadcast
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);

    }

}
