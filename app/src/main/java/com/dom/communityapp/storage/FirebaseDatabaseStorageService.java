package com.dom.communityapp.storage;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.Log;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import id.zelory.compressor.Compressor;


/**
 * Does all the Firebase stuff.
 */

public class FirebaseDatabaseStorageService extends Service {

    private List<IssueLocationListener> mLocationListeners;

    private final long FIVE_MEGABYTES = 1024 * 1024 * 5;

    //STORAGE:
    private UploadTask uploadTask;

    //DATABASE:
    //ref pointing to root
    private DatabaseReference mFirebaseRootReference;

    private DatabaseReference mFirebaseIssueReference;
    private StorageReference mFirebaseStorageReference;
    private static final String IMG_LOCATION = "images/";
    private GeoFire mGeoFire;
    private FirebaseAuth mFirebaseAuth;

    /* Service */
    private IBinder mBinder;
    private String logTag = "LOCATION_SERVICE";


    public FirebaseDatabaseStorageService() {
        mFirebaseAuth = FirebaseAuth.getInstance();
        this.mFirebaseRootReference = FirebaseDatabase.getInstance().getReference();
        this.mFirebaseStorageReference = FirebaseStorage.getInstance().getReference();
        this.mFirebaseIssueReference = mFirebaseRootReference.child("issues");
        DatabaseReference georeference = mFirebaseRootReference.child("coordinates");
        this.mGeoFire = new GeoFire(georeference);

        //attachListeners();
        this.mLocationListeners = new ArrayList<>();

        this.mBinder = new LocalBinder();

    }

    public void addLocationListener(IssueLocationListener listener) {
        mLocationListeners.add(listener);
    }

    public void removeLocationListener(IssueLocationListener listener) {
        mLocationListeners.remove(listener);
    }


    /* Called by activities */

    /**
     *
     * @param issue, the issue to be uploaded
     * @param observer, the observer to be called if something goes wrong
     */
    public void saveIssueAndImageToDatabase(final CommunityIssue issue, final FirebaseImageUploadObserver observer) {
        final IssueImage issueImage = issue.getIssueImage();

        //Se if we have auth
        checkFirebaseSignInAndDoStuff(Uri.parse(issueImage.getLocalFilePath()), new FirebaseConsumer<Uri>() {
            @Override
            public void accept(Uri subject) {//If we have auth do stuff

                try {
                    uploadFileAndAddIssue(subject, issueImage, issue);
                } catch (FirebaseImageCompressionException e) {
                    CompressionErrorDetected(observer, e);
                }

            }
        });
    }


    /**
     * Can remove an issue and its image from firebase
     * @param issue The issue to be removed
     */
    public void removeIssue(CommunityIssue issue) {
        DatabaseReference dbref = mFirebaseIssueReference.child(issue.getFirebaseID());
        dbref.removeValue();
        mGeoFire.removeLocation(issue.getFirebaseID());
        String issueurl = issue.getIssueImage().getImage_URL();
        StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(issueurl);
        photoRef.delete();
    }

    /**
     * Registers a new location query that calls all observer when locations in the query changes.
     * Remember to add the class calling this as an observer before doing this.
     * @param location The location that will be the center of the query
     * @param radius the radius of the wuery
     */
    public void addLocationQuery(Location location, double radius) {
        final double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        GeoLocation geoLocation = new GeoLocation(latitude, longitude);
        GeoQuery query = mGeoFire.queryAtLocation(geoLocation, radius);
        query.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                for (IssueLocationListener listener : mLocationListeners) {
                    LatLng latlng = new LatLng(location.latitude, location.longitude);
                    attachListenerToNewIssue(key, latlng, listener, new FirebaseBiConsumer<CommunityIssue, IssueLocationListener>() {
                        @Override
                        public void accept(CommunityIssue subject, IssueLocationListener callback) {
                            callback.newIssue(subject);
                            tryToGetImageAndSendSeperately(subject, callback);
                        }
                    });
                }
            }

            @Override
            public void onKeyExited(String key) {
                for (IssueLocationListener listener : mLocationListeners) {
                    listener.issueRemoved(new CommunityIssue(key));
                }
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
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
                //Nothing to do
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                //NOthing to do at the moment
            }
        });
    }




    private void CompressionErrorDetected(FirebaseImageUploadObserver observer, FirebaseImageCompressionException e) {
        observer.onImageErrorDetected(e); //We cannot do much about this so we call the user
    }

    private void uploadFileAndAddIssue(Uri filepath, final IssueImage issueImage, final CommunityIssue issue) throws FirebaseImageCompressionException {
        uploadFileLocal(filepath, new FirebaseFileUploadCallback() { // first we push the issueImage
            @Override
            public void onUploadSuccess(Uri donwloaduri) { // When imaged is in storage we can save the issue
                issueImage.setImage_URL(String.valueOf(donwloaduri)); //Save reference to url in issueImage
                saveIssueToDatabase(issue); //Now push the issue
            }
        });

    }

    //Pushes a single issue to the database
    private void saveIssueToDatabase(CommunityIssue issue) {

        String id = mFirebaseIssueReference.push().getKey();

        mFirebaseIssueReference.child(id).setValue(issue); // ref https://stackoverflow.com/questions/37094631/get-the-pushed-id-for-specific-value-in-firebase-android

        double latitude = issue.getCoordinate().latitude;
        double longitude = issue.getCoordinate().longitude;
        GeoLocation location = new GeoLocation(latitude, longitude);
        this.mGeoFire.setLocation(id, location);

    }

    //Check if we are signed in callbacks to a consumer if we are
    private void checkFirebaseSignInAndDoStuff(Uri filepath, FirebaseConsumer<Uri> consumer) {
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        if (user != null) {
            consumer.accept(filepath);
        } else {
            signInAnonymously(consumer, filepath);
        }

    }

    //Make a anonoumous account for the user
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
    private void uploadFileLocal(Uri filepath, final FirebaseFileUploadCallback callback) throws FirebaseImageCompressionException {
        StorageReference imageRef = mFirebaseStorageReference.child(IMG_LOCATION + filepath.getLastPathSegment());


        filepath = compressImage(filepath);

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

    //Uses compressor to compress an image
    private Uri compressImage(Uri filepath) throws FirebaseImageCompressionException {

        File imagefile = new File(filepath.getPath());
        File compressedImageFile = null;

        try {
            compressedImageFile = new Compressor(this).compressToFile(imagefile);
        } catch (IOException e) {
            throw new FirebaseImageCompressionException("ERRRRRRROOOORR BADNESS 1000000");
        }

        return Uri.fromFile(compressedImageFile);

    }


    //Gets the image, called after the issue has been downloaded and send to the observer
    private void tryToGetImageAndSendSeperately(final CommunityIssue issue, final IssueLocationListener callback) {
        final IssueImage image = issue.getIssueImage();

        if (image != null) {

            StorageReference httpsReference = FirebaseStorage.getInstance().getReferenceFromUrl(image.getImage_URL());

            httpsReference.getBytes(FIVE_MEGABYTES).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    image.setBitmap(bitmap);
                    callback.onImageDownloaded(issue);
                }
            });

        }
    }

    //Attaches listeners to a new issue
    private void attachListenerToNewIssue(String firebaseId, final LatLng location, final IssueLocationListener listener, final FirebaseBiConsumer<CommunityIssue, IssueLocationListener> callback) {
        DatabaseReference ref = mFirebaseIssueReference.child(firebaseId).getRef();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) { // At instansation gives all data, changes are receiced herafter
                CommunityIssue issue = dataSnapshot.getValue(CommunityIssue.class);

                if(issue != null) {
                issue.setFirebaseID(dataSnapshot.getKey());
                issue.setCoordinate(location);
                callback.accept(issue, listener);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TODO what to do?
            }
        });
    }




    /* Service related stuff */

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(logTag, "Location service destroyed");
        //mPreferenceUtility.saveToSharedPreferences(mCityNameList);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(logTag, "Service bound.");
        return mBinder;
    }


    public class LocalBinder extends Binder {
        public FirebaseDatabaseStorageService getService() {
            // Return this instance of LocalService so clients can call public methods
            return FirebaseDatabaseStorageService.this;
        }
    }


    /* Private interfaces and exceptions */

    /**
     * Used when uploading images
     */
    private interface FirebaseFileUploadCallback {

        void onUploadSuccess(Uri donwloaduri);
    }

    /**
     * Used for callbacks, own implementation of Consumer from Java.util because of java version
     * @param <U> the type of the parameter
     */
    private interface FirebaseConsumer<U> {
        void accept(U subject);

    }

    /**
     * Used for callbacks, own implementation of Consumer from Java.util because of java version
     * @param <U> type of parameter
     * @param <T> type of parameter
     */
    private interface FirebaseBiConsumer<U, T> {
        void accept(U subject, T callback);

    }

    /**
     * Used when user have no auth
     */
    private class AuthException extends RuntimeException {
        private final String message;

        private AuthException(String message) {
            this.message = message;
        }
    }

    /**
     * Used when can not compress images
     */
    public class FirebaseImageCompressionException extends IOException {
        private final String message;

        private FirebaseImageCompressionException(String message) {
            this.message = message;
        }
    }




}

