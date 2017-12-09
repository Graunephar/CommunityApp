package com.dom.communityapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.dom.communityapp.models.CommunityIssue;
import com.dom.communityapp.models.IssueImage;
import com.dom.communityapp.storage.FirebaseDatabaseStorage;
import com.dom.communityapp.storage.FirebaseObserver;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class UploadActivity extends AppCompatActivity implements FirebaseObserver {

    //REF:  https://github.com/probelalkhan/firebase-file-upload-example/blob/master/app/src/main/java/net/simplifiedcoding/firebasestorage/MainActivity.java

    private static final int PICK_IMAGE_REQUEST = 234;

    public ImageView img_view;
    private FirebaseDatabaseStorage firebaseDatabaseStorage;
    public Uri filePath;

    EditText edit_description;
    TextView txt_get_txt;

    public UploadActivity() {
        firebaseDatabaseStorage = new FirebaseDatabaseStorage(this);
        firebaseDatabaseStorage.addObserver(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        //STORAGE
        img_view = (ImageView) findViewById(R.id.img_view);


        //DATABASE
        edit_description = (EditText) findViewById(R.id.edit_description);
        txt_get_txt = (TextView) findViewById(R.id.txt_get_txt);



        ButterKnife.bind(this);
    }


    @OnClick(R.id.btn_submit)
    public void saveToDB() {
        String value = edit_description.getText().toString();

        IssueImage issueImage = new IssueImage();
        issueImage.setLocalFilePath("HEJ");
        issueImage.setImage_URL("HEJ");
        CommunityIssue issue = new CommunityIssue(value, value, value, value, value, issueImage);

        firebaseDatabaseStorage.saveToDatabase(issue);
    }

    @OnClick(R.id.btn_change_listener)
    public void getTextFromDB() {
        firebaseDatabaseStorage.addChangeListener();

    }

    @OnClick(R.id.btn_img)
    public void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("issueImage/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture_title)), PICK_IMAGE_REQUEST);
    }

    @OnClick(R.id.btn_upload)
    public void uploadFileToFirebase() {
        firebaseDatabaseStorage.uploadFile(filePath);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //REF: https://www.simplifiedcoding.net/firebase-storage-tutorial-android/
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //either set issueImage locally from phone, or get it from firebase, using in FirebasaDatabaseStorage - Picasso.with(uploadActivity).load(downloadUrl).into(uploadActivity.img_view);
                img_view.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDataChanged(String value) {
        txt_get_txt.setText(value);
    }

    @Override
    public void getImage(Uri downloadUrl) {
        //showing the uploaded issueImage in ImageView using the download url. Choose this method or local method.
        Picasso.with(this).load(downloadUrl).into(img_view);
    }
}
