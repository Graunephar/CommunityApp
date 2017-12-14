package com.dom.communityapp.storage;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.dom.communityapp.models.CommunityIssue;
import com.dom.communityapp.models.IssueImage;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
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
import java.util.List;


/**
 * Created by mrl on 07/12/2017.
 */

public class FirebaseDatabaseStorage {

    private List<IssueLocationListener> mLocationListeners;
    private static final String IMG_LOCATION = "images/";

    private final long FIVE_MEGABYTES = 1024 * 1024 * 5;
    //private final BitmapFactory mBitmapFactory;

    //STORAGE:
    private Context mContext;
    private UploadTask uploadTask;
    private StorageReference mFirebaseStorageReference;
    private ArrayList<FirebaseObserver> observers;

    //DATABASE:
    //ref pointing to root
    private DatabaseReference mFirebaseRootReference;

    private DatabaseReference mFirebaseIssueReference;
    private GeoFire mGeoFire;
    private FirebaseAuth mFirebaseAuth;


    public FirebaseDatabaseStorage(Context uploadActivity) {
        this.mContext = uploadActivity;
        mFirebaseAuth = FirebaseAuth.getInstance();
        this.mFirebaseRootReference = FirebaseDatabase.getInstance().getReference();
        this.mFirebaseStorageReference = FirebaseStorage.getInstance().getReference();
        this.mFirebaseIssueReference = mFirebaseRootReference.child("issues");
        DatabaseReference georeference = mFirebaseRootReference.child("coordinates");
        this.mGeoFire = new GeoFire(georeference);

        this.observers = new ArrayList<>();
        attachListeners();
        this.mLocationListeners = new ArrayList<>();
    }

    private void attachListeners() {
        mFirebaseIssueReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                final CommunityIssue issue = dataSnapshot.getValue(CommunityIssue.class);
                issue.setFirebaseID(dataSnapshot.getKey());
                final IssueImage image = issue.getIssueImage();

                if (image != null) {

                    StorageReference httpsReference = FirebaseStorage.getInstance().getReferenceFromUrl(image.getImage_URL());

                    httpsReference.getBytes(FIVE_MEGABYTES).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            image.setBitmap(bitmap);
                            for (FirebaseObserver observer : observers) {
                                observer.imageDownloaded(issue);
                            }
                        }
                    });

                }

                for (FirebaseObserver observer : observers) {
                    observer.onNewIssue(issue);
                }
            }

            //TODO How many of these should be used?

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void addObserver(FirebaseObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(FirebaseObserver observer) {
        observers.remove(observer);
    }

    public void addLocationListener(IssueLocationListener listener) {
        mLocationListeners.add(listener);
    }

    public void removeLocationListener(IssueLocationListener listener) {
        mLocationListeners.remove(listener);
    }

    //REF: https://theengineerscafe.com/save-and-retrieve-data-firebase-android/
    public void saveToDatabase(CommunityIssue data) {
        mFirebaseIssueReference.push().setValue(data);  //creates a unique id in database
    }


    public void saveIssueAndImageToDatabase(final CommunityIssue issue) {
        final IssueImage issueImage = issue.getIssueImage();

        checkFirebaseSignInAndDoStuff(Uri.parse(issueImage.getLocalFilePath()), new FirebaseConsumer<Uri>() {
            @Override
            public void accept(Uri subject) {

                uploadFileAndAddIssue(subject, issueImage, issue);

            }
        });

    }

    private void uploadFileAndAddIssue(Uri filepath, final IssueImage issueImage, final CommunityIssue issue) {
        uploadFileLocal(filepath, new FirebaseFileUploadCallback() { // first we push the issueImage
            @Override
            public void onUploadSuccess(Uri donwloaduri) { // When imaged is in storage we can save the issue
                issueImage.setImage_URL(String.valueOf(donwloaduri)); //Save reference to url in issueImage
                saveIssueToDatabase(issue); //Now push the issue

            }
        });

    }

    private void saveIssueToDatabase(CommunityIssue issue) {

        String id = mFirebaseIssueReference.push().getKey();

        mFirebaseIssueReference.child(id).setValue(issue); // ref https://stackoverflow.com/questions/37094631/get-the-pushed-id-for-specific-value-in-firebase-android

        double latitude = issue.getCoordinate().latitude;
        double longitude = issue.getCoordinate().longitude;
        GeoLocation location = new GeoLocation(latitude, longitude);
        this.mGeoFire.setLocation(id, location);

    }

    //REF: https://theengineerscafe.com/save-and-retrieve-data-firebase-android/
    public void addChangeListener() {
        mFirebaseIssueReference.child("value").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (FirebaseObserver observer : observers) {
                    observer.onDataChanged(dataSnapshot.getValue(String.class));
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void checkFirebaseSignInAndDoStuff(Uri filepath, FirebaseConsumer<Uri> consumer) {
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        if (user != null) {
            consumer.accept(filepath);
        } else {
            signInAnonymously(consumer, filepath);
        }

    }

    private void signInAnonymously(final FirebaseConsumer<Uri> consumer, final Uri filepath) {
        mFirebaseAuth.signInAnonymously().addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                consumer.accept(filepath);
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        throw new AuthException(exception.getMessage());
                    }
                });
    }

    //Made to upload files in background without updating a gui at the same time...
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
                throw new AuthException(exception.getMessage());
            }
        });

    }

    public void uploadFile(final Uri filepath) {

        checkFirebaseSignInAndDoStuff(filepath, new FirebaseConsumer<Uri>() {
            @Override
            public void accept(Uri subject) {
                uploadFileFromActivity(filepath);
            }
        });

    }

    private void uploadFileFromActivity(Uri filepath) {
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

                    for (FirebaseObserver observer : observers) { //Notify all observers about upload
                        observer.getImage(downloadUrl);
                    }

                    progressDialog.dismiss();
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

    public void addLocationQuery(Location location, double radius) {
        final double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        GeoLocation geoLocation = new GeoLocation(latitude, longitude);
        GeoQuery query = mGeoFire.queryAtLocation(geoLocation, radius);
        query.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                //  Toast.makeText(mContext.getApplicationContext(), "onKeyEntered" + location.toString(), Toast.LENGTH_LONG).show();
                for (IssueLocationListener listener : mLocationListeners) {
                    LatLng latlng = new LatLng(location.latitude, location.longitude);
                    attachListenerToNewIssue(key, latlng, listener, new FirebaseBiConsumer<CommunityIssue, IssueLocationListener>() {
                        @Override
                        public void accept(CommunityIssue subject, IssueLocationListener callback) {
                            callback.newIssue(subject);
                        }
                    });
                }
            }

            @Override
            public void onKeyExited(String key) {
                for (IssueLocationListener listener : mLocationListeners) {
                    listener.issueRemoved(new CommunityIssue(key));
                }
                //  Toast.makeText(mContext.getApplicationContext(), "onKeyExited" + key, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                //Toast.makeText(mContext.getApplicationContext(), "onKeyMoved" + location.toString(), Toast.LENGTH_LONG).show();
                for (IssueLocationListener listener : mLocationListeners) {
                    LatLng latlng = new LatLng(location.latitude, location.longitude);
                    attachListenerToNewIssue(key, latlng, listener, new FirebaseBiConsumer<CommunityIssue, IssueLocationListener>() {
                        @Override
                        public void accept(CommunityIssue subject, IssueLocationListener callback) {
                            callback.movedIssue(subject);
                        }
                    });
                }
            }

            @Override
            public void onGeoQueryReady() {
                //    Toast.makeText(mContext.getApplicationContext(), "onGeoQueryReady", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                // Toast.makeText(mContext.getApplicationContext(), "onGeoQueryError" + error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void attachListenerToNewIssue(String firebaseId, final LatLng location, final IssueLocationListener listener, final FirebaseBiConsumer<CommunityIssue, IssueLocationListener> callback) {
        DatabaseReference ref = mFirebaseIssueReference.child(firebaseId).getRef();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) { // At instansation gives all data, changes are receiced herafter

                CommunityIssue issue = dataSnapshot.getValue(CommunityIssue.class);
                issue.setFirebaseID(dataSnapshot.getKey());
                issue.setCoordinate(location);
                callback.accept(issue, listener);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TODO what to do?
            }
        });
    }

    private interface FirebaseFileUploadCallback {

        void onUploadSuccess(Uri donwloaduri);
    }

    private interface FirebaseConsumer<U> {
        void accept(U subject);

    }


    private interface FirebaseBiConsumer<U, T> {
        void accept(U subject, T callback);

    }

    private class AuthException extends RuntimeException {
        private final String message;

        private AuthException(String message) {
            this.message = message;
        }
    }


}

