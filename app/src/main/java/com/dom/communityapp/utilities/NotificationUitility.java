package com.dom.communityapp.utilities;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.dom.communityapp.MapsActivity;
import com.dom.communityapp.R;

import java.sql.Date;
import java.text.SimpleDateFormat;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by daniel on 11/22/17.
 */

public class NotificationUitility {

    private NotificationCompat.Builder mBuilder;
    private NotificationManager mNotificationManager  ;
    private Context mContext;
    private int NOTIFICATION_ID = 001;

    public NotificationUitility(Context context) {
        this.mContext = context;
    }

    // Solution inspired by: https://developer.android.com/guide/topics/ui/notifiers/notifications.html
    public void makeNotification() {
        if(mBuilder == null) mBuilder = createNoticiationBuilder();

        if(mNotificationManager == null) mNotificationManager = (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);

        mBuilder.setContentText(mContext.getString(R.string.noticication_channel_description) + " " + getTimeInChosenFormat());

        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    private String getTimeInChosenFormat() {
        //HH:mm:ss ref: https://stackoverflow.com/questions/40527167/convert-hours-and-minutes-into-milliseconds-and-return-into-hhmmss-format
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        String time = formatter.format(date);
        return time;
    }


    private NotificationCompat.Builder createNoticiationBuilder() {

        Intent intent = new Intent(mContext, MapsActivity.class);

        PendingIntent pendingintent = PendingIntent.getActivity(
                mContext,
                IntentConstant.CITY_DETAILS_REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        return new NotificationCompat.Builder(mContext)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(mContext.getString(R.string.noticication_channel_name))
                .setContentIntent(pendingintent);
    }


}
