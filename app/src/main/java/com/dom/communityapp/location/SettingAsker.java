package com.dom.communityapp.location;

/**
 * Created by daniel on 11/30/17.
 */

public interface SettingAsker {

    void askToChangeSettings();

    boolean onResult(int requestCode, String[] permissions, int[] grantResults);

    boolean havePermission();
}
