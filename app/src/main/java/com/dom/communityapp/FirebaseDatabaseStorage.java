package com.dom.communityapp;

import android.app.ProgressDialog;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;


/**
 * Created by mrl on 07/12/2017.
 */

public class FirebaseDatabaseStorage {
    //STORAGE:
    UploadActivity uploadActivity;
    private UploadTask uploadTask;
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    //DATABASE:

    //ref pointing to root
    private DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

    DatabaseReference demoRef = rootRef.child("demo");


    public FirebaseDatabaseStorage(UploadActivity uploadActivity) {
        this.uploadActivity = uploadActivity;
    }

    //REF: https://theengineerscafe.com/save-and-retrieve-data-firebase-android/
    public void saveToDatabase() {

        // Chose one or the other:
        demoRef.push().setValue(uploadActivity.value);  //creates a unique id in database
        demoRef.child("value").setValue(uploadActivity.value);  //creates one value, which is easy to fetch
    }

    //REF: https://theengineerscafe.com/save-and-retrieve-data-firebase-android/
    public void getFromDatabase() {
        demoRef.child("value").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                uploadActivity.value = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void uploadFile() {
        //REF: https://www.simplifiedcoding.net/firebase-storage-tutorial-android/
        //REF: https://theengineerscafe.com/firebase-storage-android-tutorial/
        if (uploadActivity.filePath != null) {

            final ProgressDialog progressDialog = new ProgressDialog(uploadActivity);
            progressDialog.setMax(100);
            progressDialog.setMessage("Uploading...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.show();
            progressDialog.setCancelable(false);

            StorageReference imageRef = storageReference.child("images/" + uploadActivity.filePath.getLastPathSegment());

            uploadTask = imageRef.putFile(uploadActivity.filePath);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    //showing the uploaded image in ImageView using the download url. Choose this method or local method.
                    Picasso.with(uploadActivity).load(downloadUrl).into(uploadActivity.img_view);

                    progressDialog.dismiss();
                    Toast.makeText(uploadActivity, "File Uploaded ", Toast.LENGTH_LONG).show();
                }
            });
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    progressDialog.dismiss();
                    Toast.makeText(uploadActivity, exception.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                }
            });
        }
        //if there is no file
        else {

        }
    }
}

