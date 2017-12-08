package com.dom.communityapp.storage;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.dom.communityapp.UploadActivity;
import com.dom.communityapp.models.CommunityIssue;
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

import java.util.ArrayList;


/**
 * Created by mrl on 07/12/2017.
 */

public class FirebaseDatabaseStorage {
    //STORAGE:
    Context mUploadActivity;
    private UploadTask uploadTask;
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    private ArrayList<FirebaseObserver> observers = new ArrayList<>();
    //DATABASE:

    //ref pointing to root
    private DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

    DatabaseReference demoRef = rootRef.child("demo");


    public FirebaseDatabaseStorage(Context uploadActivity) {
        this.mUploadActivity = uploadActivity;
    }

    public void addObserver(FirebaseObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(FirebaseObserver observer){
        observers.remove(observer);
    }

    //REF: https://theengineerscafe.com/save-and-retrieve-data-firebase-android/
    public void saveToDatabase(String data) {

        // Chose one or the other:
        demoRef.push().setValue(data);  //creates a unique id in database
        //demoRef.child("value").setValue(data);  //creates one value, which is easy to fetch

    }

    public void saveIssueToDatabase(CommunityIssue issue) {
        demoRef.push().setValue(issue);
    }

    //REF: https://theengineerscafe.com/save-and-retrieve-data-firebase-android/
    public void addChangeListener() {
        demoRef.child("value").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(FirebaseObserver observer : observers) {
                    observer.onDataChanged(dataSnapshot.getValue(String.class));
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void uploadFile(Uri filepath) {
        //REF: https://www.simplifiedcoding.net/firebase-storage-tutorial-android/
        //REF: https://theengineerscafe.com/firebase-storage-android-tutorial/
        if (filepath != null) {

            final ProgressDialog progressDialog = new ProgressDialog(mUploadActivity);
            progressDialog.setMax(100);
            progressDialog.setMessage("Uploading...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.show();
            progressDialog.setCancelable(false);

            StorageReference imageRef = storageReference.child("images/" + filepath.getLastPathSegment());

            uploadTask = imageRef.putFile(filepath);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    for(FirebaseObserver observer : observers) {
                        observer.getImage(downloadUrl);
                    }

                    progressDialog.dismiss();
                    Toast.makeText(mUploadActivity, "File Uploaded ", Toast.LENGTH_LONG).show();
                }
            });
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    progressDialog.dismiss();
                    Toast.makeText(mUploadActivity, exception.getMessage(), Toast.LENGTH_LONG).show();
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

