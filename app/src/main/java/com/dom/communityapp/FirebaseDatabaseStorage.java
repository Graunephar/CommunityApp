package com.dom.communityapp;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import static android.support.v4.app.ActivityCompat.startActivityForResult;


/**
 * Created by mrl on 07/12/2017.
 */

public class FirebaseDatabaseStorage {


    Context context;
    UploadActivity uploadActivity;

    public FirebaseDatabaseStorage(UploadActivity uploadActivity) {
        this.uploadActivity = uploadActivity;
    }

    //getting firebase storage reference
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    //this method will upload the file
    public void uploadFile() {
        ;
        //if there is a file to upload
        if (uploadActivity.filePath != null) {
            //displaying a progress dialog while upload is going on
//            final ProgressDialog progressDialog = new ProgressDialog();
            //          progressDialog.setTitle("Uploading");
            //        progressDialog.show();


            //create reference to images folder and adding a name to the file that will be uploaded

            StorageReference imageRef = storageReference.child("images/" + uploadActivity.filePath.getLastPathSegment());
            imageRef.putFile(uploadActivity.filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //if the upload is successfull
                            //hiding the progress dialog
                            //                         progressDialog.dismiss();

                            //and displaying a success toast
//                            Toast.makeText(context, "File Uploaded ", Toast.LENGTH_LONG).show();
                            //Toast.makeText(getApplicationContext(), "File Uploaded ", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            //if the upload is not successfull
                            //hiding the progress dialog
                            //                 progressDialog.dismiss();

                            //and displaying error message
                            //      Toast.makeText(context, exception.getMessage(), Toast.LENGTH_LONG).show();
                            //Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //calculating progress percentage
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                            //displaying percentage in progress dialog
                            //                progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    });
        }
        //if there is not any file
        else {
            //you can display an error toast
        }
    }
}
