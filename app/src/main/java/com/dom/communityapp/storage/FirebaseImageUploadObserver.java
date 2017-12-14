package com.dom.communityapp.storage;

/**
 * Created by daniel on 12/14/17.
 */

public interface FirebaseImageUploadObserver {
    void onImageErrorDetected(FirebaseDatabaseStorageService.FirebaseImageCopressionException e);
}
