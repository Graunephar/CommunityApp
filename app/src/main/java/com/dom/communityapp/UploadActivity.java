package com.dom.communityapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;

public class UploadActivity extends AppCompatActivity implements View.OnClickListener {

    //REF:  https://github.com/probelalkhan/firebase-file-upload-example/blob/master/app/src/main/java/net/simplifiedcoding/firebasestorage/MainActivity.java

    private static final int PICK_IMAGE_REQUEST = 234;
    private Button buttonChoose;
    private Button buttonUpload;

    public ImageView img_view;
    private FirebaseDatabaseStorage firebaseDatabaseStorage = new FirebaseDatabaseStorage(this);
    //used in Firebasedatabasestorage
    public Uri filePath;

    EditText edit_description;
    Button btn_get_txt;
    TextView txt_get_txt;
    Button btn_submit;
    public String value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        //STORAGE
        buttonChoose = (Button) findViewById(R.id.btn_img);
        buttonUpload = (Button) findViewById(R.id.btn_upload);
        img_view = (ImageView) findViewById(R.id.img_view);

        buttonChoose.setOnClickListener(this);
        buttonUpload.setOnClickListener(this);

        //DATABASE
        edit_description = (EditText) findViewById(R.id.edit_description);
        txt_get_txt = (TextView) findViewById(R.id.txt_get_txt);
        btn_submit = (Button) findViewById(R.id.btn_submit);
        btn_get_txt = (Button) findViewById(R.id.btn_get_txt);


        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                value = edit_description.getText().toString();
                firebaseDatabaseStorage.saveToDatabase();
            }
        });

        btn_get_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseDatabaseStorage.getFromDatabase();
                txt_get_txt.setText(value);
            }
        });
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //either set image locally from phone, or get it from firebase, using in FirebasaDatabaseStorage - Picasso.with(uploadActivity).load(downloadUrl).into(uploadActivity.img_view);
                img_view.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (view == buttonChoose) {
            showFileChooser();
        } else if (view == buttonUpload) {
            firebaseDatabaseStorage.uploadFile();
        }
    }
}
