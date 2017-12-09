package com.dom.communityapp.storage;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.dom.communityapp.models.CommunityIssue;
import com.dom.communityapp.models.Image;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.function.Consumer;


/**
 * Created by mrl on 07/12/2017.
 */

public class FirebaseDatabaseStorage {
    private static final String IMG_LOCATION = "images/";

    //STORAGE:
    Context mContext;
    private UploadTask uploadTask;
    private StorageReference mFirebaseStorageReference;
    private ArrayList<FirebaseObserver> observers;
    //DATABASE:

    //ref pointing to root
    private DatabaseReference mFirebaseRootReference;

    DatabaseReference mFirebaseIssueReference;
    private FirebaseAuth mFirebaseAuth;


    public FirebaseDatabaseStorage(Context uploadActivity) {
        this.mContext = uploadActivity;
        mFirebaseAuth = FirebaseAuth.getInstance();
        this.mFirebaseRootReference = FirebaseDatabase.getInstance().getReference();
        this.mFirebaseStorageReference = FirebaseStorage.getInstance().getReference();
        this.mFirebaseIssueReference = mFirebaseRootReference.child("issues");
        this.observers = new ArrayList<>();
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
        mFirebaseIssueReference.push().setValue(data);  //creates a unique id in database
        //mFirebaseIssueReference.child("value").setValue(data);  //creates one value, which is easy to fetch

    }

    public void saveIssueAndImageToDatabase(final CommunityIssue issue) {
        final Image image = issue.getImage();
        uploadFileLocal(image.getLocalFilePath(), new FirebaseFileUploadCallback() { // first we push the image
            @Override
            public void onUploadSuccess(Uri donwloaduri) { // When imaged is in storage we can save the issue
                image.setImage_URL(donwloaduri); //Save reference to url in image
                saveIssueToDatabase(issue); //Now push the issue

            }
        });

    }

    public void saveIssueToDatabase(CommunityIssue issue) {
        mFirebaseIssueReference.push().setValue(issue);
    }

    //REF: https://theengineerscafe.com/save-and-retrieve-data-firebase-android/
    public void addChangeListener() {
        mFirebaseIssueReference.child("value").addListenerForSingleValueEvent(new ValueEventListener() {
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

    private void checkFirebaseSignInAndDoStuff(Consumer<Uri> consumer, Uri filepath) {
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        if (user != null) {
            consumer.accept(filepath);
        } else {
            signInAnonymously();
        }

    }

    private void uploadFileLocal(Uri filepath, final FirebaseFileUploadCallback callback) {
        StorageReference imageRef = mFirebaseStorageReference.child(IMG_LOCATION + filepath.getLastPathSegment());
        uploadTask = imageRef.putFile(filepath);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                callback.onUploadSuccess(downloadUrl);
            }
        });
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(mContext, exception.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    public void uploadFile(Uri filepath) {
        //REF: https://www.simplifiedcoding.net/firebase-storage-tutorial-android/
        //REF: https://theengineerscafe.com/firebase-storage-android-tutorial/
        if (filepath != null) {

            final ProgressDialog progressDialog = new ProgressDialog(mContext);
            progressDialog.setMax(100);
            progressDialog.setMessage("Uploading...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.show();
            progressDialog.setCancelable(false);

            StorageReference imageRef = mFirebaseStorageReference.child(IMG_LOCATION + filepath.getLastPathSegment());

            uploadTask = imageRef.putFile(filepath);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    for(FirebaseObserver observer : observers) { //Notify all observers about upload
                        observer.getImage(downloadUrl);
                    }

                    progressDialog.dismiss();
                    Toast.makeText(mContext, "File Uploaded ", Toast.LENGTH_LONG).show();
                }
            });
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    progressDialog.dismiss();
                    Toast.makeText(mContext, exception.getMessage(), Toast.LENGTH_LONG).show();
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

    private interface FirebaseFileUploadCallback {

        void onUploadSuccess(Uri donwloaduri);
    }
}

