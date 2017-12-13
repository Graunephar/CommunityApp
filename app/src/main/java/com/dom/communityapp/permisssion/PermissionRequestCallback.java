package com.dom.communityapp.permisssion;

/**
 * Created by daniel on 11/30/17.
 */

public interface PermissionRequestCallback {

    void onPermissionGranted();

    void onPermissionRefused();

    boolean expirable();
}
