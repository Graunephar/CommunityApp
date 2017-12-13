package com.dom.communityapp.permisssion;

import android.support.annotation.Nullable;

import pl.tajchert.nammu.PermissionCallback;

/**
 * Created by daniel on 11/30/17.
 */

public interface SettingAsker {

    void askToChangeSettings(@Nullable final PermissionCallback callback);

    boolean onResult(int requestCode, String[] permissions, int[] grantResults);

    boolean havePermission();

    //https://developers.google.com/maps/documentation/android-api/current-place-tutorial#location-permission
    void askForPermission(PermissionRequestCallback callback);
}
