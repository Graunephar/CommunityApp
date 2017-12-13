package com.dom.communityapp;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.content.Intent;
import android.provider.MediaStore;
import android.widget.Spinner;
import android.widget.Toast;

import com.dom.communityapp.location.BroadCastReceiveUitility;
import com.dom.communityapp.location.LocationListener;
import com.dom.communityapp.location.LocationSettingAsker;
import com.dom.communityapp.location.LocationUpdateCallback;
import com.dom.communityapp.location.PermissionRequestCallback;
import com.dom.communityapp.models.CommunityIssue;
import com.dom.communityapp.models.IssueImage;
import com.dom.communityapp.storage.FirebaseDatabaseStorage;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import pl.tajchert.nammu.PermissionCallback;


public class CreateEventActivity extends AbstractNavigation implements LocationListener {

    //Local variable

    Bitmap myImage;
    @BindView(R.id.imageView_Event) ImageView viewer;
    @BindView(R.id.Edittext_Short_despription) EditText short_description;
    @BindView(R.id.Edittext_Long_despription) EditText long_description;
    @BindView(R.id.spinner_tags) Spinner tag_spin;
    @BindView(R.id.spinner_categories) Spinner cat_spin;
    @BindView(R.id.spinner_time_required) Spinner time_spin;
    @BindView(R.id.mapView_create_event) MapView map_show;



    private Uri mImageFilePath;
    private Bitmap mTakenImage;
    private FirebaseDatabaseStorage mStorage = new FirebaseDatabaseStorage(this);

    //Request codes
    private static final int CAMERA_REQUEST_CODE = 11;
    private boolean mBound;
    private ServiceConnection mConnection;
    private LocationSettingAsker mLocationAsker;
    private Location mLastKnownLocation;
    private LocationCommunityService mService;
    private BroadCastReceiveUitility mBroadCastRecieveUtility;
    private boolean mUnpushedLocationWaiting = false;

    //private DrawerLayout mDrawerLayout;
    //private ActionBarDrawerToggle mToggle;


    public CreateEventActivity() {
        this.mBroadCastRecieveUtility = new BroadCastReceiveUitility(this);
    }

    @Override
    protected DrawerLayout getdrawerLayout() {
        return (DrawerLayout) findViewById(R.id.createeventactivity);
    }

    @Override
    protected int getLayoutid() {
        return R.layout.activity_create_event;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setContentView(R.layout.activity_create_event);
        super.onCreate(savedInstanceState);

        ButterKnife.bind(this);
        //Adapter for tag_spin
        ArrayAdapter<String> tag_spin_adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.tags));
        tag_spin_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tag_spin.setAdapter(tag_spin_adapter);

        //Adapter for cat_spin
        ArrayAdapter<String> cat_spin_adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.categories));
        tag_spin_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cat_spin.setAdapter(cat_spin_adapter);

        //Adapter for time_spin
        ArrayAdapter<String> time_spin_adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.Time_duration));
        tag_spin_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        time_spin.setAdapter(time_spin_adapter);

        EasyImage.configuration(this).setAllowMultiplePickInGallery(false); // allows multiple picking in galleries that handle it. Also only for phones with API 18+ but it won't crash lower APIs. False by default

        this.mLocationAsker = new LocationSettingAsker(this);

    }

    /** Taking piuctures**/

    @OnClick(R.id.imageView_Event)
    public void takePicture() {
        if (checkFilePermission()) {

            EasyImage.openChooserWithGallery(this, getString(R.string.choose_image), 0);

        } else {

        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        bindToService();
        mBroadCastRecieveUtility.registerForBroadcasts();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindFromService();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //from: https://github.com/jkwiecien/EasyImage
        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                //TODO Some error handling
            }

            @Override
            public void onImagesPicked(List<File> imagesFiles, EasyImage.ImageSource source, int type) {
                //Handle the images
                onPhotosReturned(imagesFiles);
            }

            @Override
            public void onCanceled(EasyImage.ImageSource source, int type) {
                // Cancel handling, you might wanna remove taken photo if it was canceled
                if (source == EasyImage.ImageSource.CAMERA) {
                    File photoFile = EasyImage.lastlyTakenButCanceledPhoto(CreateEventActivity.this);
                    if (photoFile != null) photoFile.delete();
                }
            }

        });
    }

    private void onPhotosReturned(List<File> imagesFiles) {

        File file =imagesFiles.get(0);

        mImageFilePath = Uri.fromFile(file); // get only image

        BitmapFactory factory = new BitmapFactory();
        mTakenImage = factory.decodeFile(String.valueOf(file.getAbsoluteFile()));

        viewer.setImageBitmap(mTakenImage); // Update view with file


    }


    //Inspired by: https://stackoverflow.com/questions/45391290/ask-permission-for-write-external-storage
    private boolean checkFilePermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED && checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("TAG", "Permission is granted");
                return true;
            } else {

                Log.v("TAG", "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }

        return false;
    }


    //Inspired by: https://stackoverflow.com/questions/45391290/ask-permission-for-write-external-storage
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        if(mLocationAsker.onResult(requestCode, permissions, grantResults)) {
            if(mBound) {
                mService.getDeviceLocation(); //Not sure if this is redundant
            }
        }

        switch (requestCode) {
            case 0:
                boolean isPerpermissionForAllGranted = false;
                if (grantResults.length > 0 && permissions.length==grantResults.length) {
                    for (int i = 0; i < permissions.length; i++){
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED){
                            isPerpermissionForAllGranted=true;
                        }else{
                            isPerpermissionForAllGranted=false;
                        }
                    }

                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    isPerpermissionForAllGranted=true;
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                if(isPerpermissionForAllGranted){
                    takePicture();
                }
                break;
        }
    }

    @OnClick(R.id.create_event_btn)
    public void createEvent() {


        mLocationAsker.askToChangeSettings(new PermissionCallback() {
            @Override
            public void permissionGranted() {
                if(mLastKnownLocation != null) {
                    transmitIssue();
                 } else if(mBound){
                    giveMeLocation();
                } else {
                    tellUserNoLOcation();
                }

            }

            @Override
            public void permissionRefused() {
                giveMeLocation();
            }
        });
    }

    /** Tries to get a permission using different methods
     * Calls back on create event when changed
     */
    private void giveMeLocation() {

        if(mLocationAsker.havePermission()) {

            fetchLocationAndPush();

        } else {
            mLocationAsker.askForPermission(new PermissionRequestCallback() {
                @Override
                public void onPermissionGranted() {
                    fetchLocationAndPush(); // THis should have done it try again
                }

                @Override
                public void onPermissionRefused() {
                    alertAndAsk();
                }

                @Override
                public boolean expirable() {
                    return true;
                }
            });
        }
    }

    private void fetchLocationAndPush() {
        if(mLastKnownLocation != null) {
            transmitIssue();
        } else {
            this.mUnpushedLocationWaiting = true;
            mService.getDeviceLocation();
        }

    }


    private void alertAndAsk() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.no_location_permission_message)
                .setTitle(R.string.no_location_permission_title);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish(); // We cannot do anything abort the app
            }
        });


        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mLocationAsker.askForPermission(new PermissionRequestCallback() {
                    @Override
                    public void onPermissionGranted() {
                        createEvent();
                    }

                    @Override
                    public void onPermissionRefused() {
                        alertAndAsk();
                    }

                    @Override
                    public boolean expirable() {
                        return true;
                    }
                });
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    private void tellUserNoLOcation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.wait_location_message)
                .setTitle(R.string.wait_location_title);

        AlertDialog dialog = builder.create();
        dialog.show();


    }

    private void transmitIssue() {

        if(mLastKnownLocation == null ) {
            createEvent(); // Stuff not working call back
        } else {
            String sshort = short_description.getText().toString();
            String llong = long_description.getText().toString();
            String cat_text = cat_spin.getSelectedItem().toString();
            String tag_text = tag_spin.getSelectedItem().toString();
            String time_text = time_spin.getSelectedItem().toString();

            IssueImage issueImage = new IssueImage(mImageFilePath, mTakenImage);

            double latitude = mLastKnownLocation.getLatitude();
            double longitude = mLastKnownLocation.getLongitude();
            LatLng latlng = new LatLng(latitude, longitude);

            CommunityIssue issue = new CommunityIssue(sshort, llong, cat_text, tag_text, time_text, issueImage, latlng);

            mStorage.saveIssueAndImageToDatabase(issue);
        }

    }


    /**
     * Service stuff
     **/

    private void bindToService() {
        // Establish a connection with the service.  We use an explicit
        // class name because we want a specific service implementation that
        // we know will be running in our own process (and thus won't be
        // supporting component replacement by other applications).
        if (mBound == false) {
            this.mConnection = createNewServiceConnection();
            bindService(new Intent(this,
                    LocationCommunityService.class), mConnection, Context.BIND_AUTO_CREATE);
        }
    }

    private void askforLocation() {

    }

    void unbindFromService() {
        if (mBound) {
            // Detach our existing connection.
            unbindService(mConnection);
            mBound = false;
        }
    }

    private ServiceConnection createNewServiceConnection() {

        return new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName className,
                                           IBinder service) {
                // We've bound to LocalService, cast the IBinder and get LocalService instance
                mService = ((LocationCommunityService.LocalBinder) service).getService();

                mBound = true;

                if(mLocationAsker.havePermission()) {
                    mService.getDeviceLocation();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName arg0) {
                mBound = false;
                mService = null;
            }

            @Override
            public void onBindingDied(ComponentName name) {
                mBound = false;
            }
        };
    }


    @Override
    public void locationIncoming(Location location) {
        if(location != null) this.mLastKnownLocation = location;
        if(mUnpushedLocationWaiting) { // we have an issue waiting to be pushed
            fetchLocationAndPush();
            mUnpushedLocationWaiting = false;
        }
    }
}
