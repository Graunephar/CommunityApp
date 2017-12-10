package com.dom.communityapp.location;

/**
 * Created by daniel on 11/30/17.
 */

public interface SettingAsker {

    void ask();

    boolean onResult(int requestCode, String[] permissions, int[] grantResults);

    boolean havePermission();
}
