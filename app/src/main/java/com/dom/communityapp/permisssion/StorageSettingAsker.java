package com.dom.communityapp.permisssion;

import android.app.Activity;
import android.support.annotation.Nullable;

import pl.tajchert.nammu.Nammu;
import pl.tajchert.nammu.PermissionCallback;

/**
 * Created by daniel on 12/13/17.
 * Can enable WRITE_EXTERNAL_STORAGE
 */

public class StorageSettingAsker implements SettingAsker {
    private final Activity mActivity;


    public StorageSettingAsker(Activity activity) {
        this.mActivity = activity;
    }

    @Override
    public void askToChangeSettings(@Nullable PermissionCallback callback) {
        throw new UnsupportedOperationException("This is not implemented for storage");
    }

    @Override
    public boolean onResult(int requestCode, String[] permissions, int[] grantResults) {
        Nammu.onRequestPermissionsResult(requestCode, permissions, grantResults);
        return havePermission();
    }

    @Override
    public boolean havePermission() {
        if (Nammu.checkPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void askForPermission(final PermissionRequestCallback callback) {
        Nammu.askForPermission(this.mActivity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, new PermissionCallback() {
            @Override
            public void permissionGranted() {
                callback.onPermissionGranted();
            }

            @Override
            public void permissionRefused() {
                callback.onPermissionRefused();
            }
        });

    }
}
