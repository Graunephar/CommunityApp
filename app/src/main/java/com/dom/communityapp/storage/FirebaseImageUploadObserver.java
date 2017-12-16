package com.dom.communityapp.storage;

/**
 * Used for calling back when errors relating to images occur
 */

public interface FirebaseImageUploadObserver {
    void onImageErrorDetected(FirebaseDatabaseStorageService.FirebaseImageCompressionException e);
}
