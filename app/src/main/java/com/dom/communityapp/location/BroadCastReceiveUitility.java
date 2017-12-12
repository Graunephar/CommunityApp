package com.dom.communityapp.location;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import static com.dom.communityapp.location.LocationConstants.EXTRA_LAST_LOCATION;

/**
 * Created by daniel on 11/23/17.
 */

public class BroadCastReceiveUitility {

    private final LocationListener mActivity;
    private BroadcastReceiver mBroadcastReceiver;

    public BroadCastReceiveUitility(LocationListener activity) {
        mActivity = activity;
    }

    private BroadcastReceiver createNewBroadcastReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle bundle = intent.getExtras();

                Location location = (Location) bundle.getParcelable(EXTRA_LAST_LOCATION);
                mActivity.locationIncoming(location);
            }
        };
    }

    public void registerForBroadcasts() {
        //Registering for local broadcasts
        this.mBroadcastReceiver = createNewBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BroadCastSendUtility.BROADCAST_BACKGROUND_SERVICE_RESULT);
        LocalBroadcastManager.getInstance((Context) mActivity).registerReceiver(mBroadcastReceiver, filter);
    }



}
