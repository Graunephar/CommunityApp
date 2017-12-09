package com.dom.communityapp;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.dom.communityapp.models.CommunityIssue;
import com.dom.communityapp.models.IssueImage;
import com.dom.communityapp.storage.FirebaseDatabaseStorage;
import com.google.android.gms.maps.MapView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;


/* This activity is a temporary copy of create event activity, made for
testing integration with firebase without getting merge conflicts in git */


public class FirebaseCreateEventTestTempActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 45635;

//Local variable

    Bitmap mTakenImage;
    ImageView viewer;
    MapView map_show;

    private Uri mImageFilePath;

    //Request codes
    private static final int CAMERA_REQUEST_CODE = 11;
    private FirebaseDatabaseStorage mStorage = new FirebaseDatabaseStorage(this);

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;

    @BindView(R.id.edittext_short_description2)
    EditText short_description;
    @BindView(R.id.edittext_long_description2)
    EditText long_description;
    @BindView(R.id.spinner_tags2)
    Spinner tag_spin;
    @BindView(R.id.spinner_categories2)
    Spinner cat_spin;
    @BindView(R.id.spinner_time_required2)
    Spinner time_spin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_create_event_test_temp);

        ButterKnife.bind(this);

        viewer = (ImageView) findViewById(R.id.imageview_event2);

        map_show = findViewById(R.id.mapView_create_event);

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
                    File photoFile = EasyImage.lastlyTakenButCanceledPhoto(FirebaseCreateEventTestTempActivity.this);
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

    // reference: https://stackoverflow.com/questions/15432592/get-file-path-of-image-on-android
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    // reference: https://stackoverflow.com/questions/15432592/get-file-path-of-image-on-android
    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    @OnClick(R.id.imageview_event2)
    public void takePhoto() {

        if (checkFilePermission()) {

            EasyImage.openChooserWithGallery(this, String.valueOf(R.string.choose_image), 0);

        } else {

        }
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
                        takePhoto();
                    }
                    break;
            }
        }


    @OnClick(R.id.create_event_button_OK2)
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
