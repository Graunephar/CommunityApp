package com.dom.communityapp;


import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


/**
 * Created by mrl on 07/12/2017.
 */

public class FirebaseDatabaseStorage {

    UploadActivity uploadActivity;

    public FirebaseDatabaseStorage(UploadActivity uploadActivity) {
        this.uploadActivity = uploadActivity;
    }

    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();


    public void uploadFile() {

        if (uploadActivity.filePath != null) {

            final ProgressDialog progressDialog = new ProgressDialog(uploadActivity);
            progressDialog.setTitle("Uploading");
            progressDialog.show();


            StorageReference imageRef = storageReference.child("images/" + uploadActivity.filePath.getLastPathSegment());
            imageRef.putFile(uploadActivity.filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            progressDialog.dismiss();
                            Toast.makeText(uploadActivity, "File Uploaded ", Toast.LENGTH_LONG).show();
                            Toast.makeText(uploadActivity, "File Uploaded ", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            progressDialog.dismiss();
                            Toast.makeText(uploadActivity, exception.getMessage(), Toast.LENGTH_LONG).show();
                            Toast.makeText(uploadActivity, exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    });
        }
        //if there is not any file
        else {

        }
    }
}
