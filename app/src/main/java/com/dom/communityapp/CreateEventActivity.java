package com.dom.communityapp;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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

import com.dom.communityapp.models.CommunityIssue;
import com.dom.communityapp.models.IssueImage;
import com.dom.communityapp.storage.FirebaseDatabaseStorage;
import com.google.android.gms.maps.MapView;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;


public class CreateEventActivity extends AbstractNavigation {

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

    //private DrawerLayout mDrawerLayout;
    //private ActionBarDrawerToggle mToggle;

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


    }


    /** Taking piuctures**/

    @OnClick(R.id.imageView_Event)
    public void takePicture() {
        if (checkFilePermission()) {

            EasyImage.openChooserWithGallery(this, String.valueOf(R.string.choose_image), 0);

        } else {

        }
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

        String sshort = short_description.getText().toString();
        String llong = long_description.getText().toString();
        String cat_text = cat_spin.getSelectedItem().toString();
        String tag_text = tag_spin.getSelectedItem().toString();
        String time_text = time_spin.getSelectedItem().toString();

        IssueImage issueImage = new IssueImage(mImageFilePath, mTakenImage);

        CommunityIssue issue = new CommunityIssue(sshort, llong, cat_text, tag_text, time_text, issueImage);

        mStorage.saveIssueAndImageToDatabase(issue);

    }
}
