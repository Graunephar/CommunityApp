package com.dom.communityapp.storage;

import android.net.Uri;

/**
 * Created by daniel on 12/8/17.
 */

public interface FirebaseObserver {

    void onDataChanged(String value);
    void getImage(Uri downloadUrl);
}
