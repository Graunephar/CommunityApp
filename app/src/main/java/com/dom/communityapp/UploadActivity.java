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


import java.io.IOException;

public class UploadActivity extends AppCompatActivity implements View.OnClickListener {

    //REF:  https://github.com/probelalkhan/firebase-file-upload-example/blob/master/app/src/main/java/net/simplifiedcoding/firebasestorage/MainActivity.java

    private static final int PICK_IMAGE_REQUEST = 234;
    private Button buttonChoose;
    private Button buttonUpload;

    private ImageView img_view;
    private FirebaseDatabaseStorage firebaseDatabaseStorage = new FirebaseDatabaseStorage(this);
    //used in Firebasedatabasestorage
    public Uri filePath;

    EditText edit_description;
    Button btn_get_txt;
    TextView txt_get_txt;
    Button btn_submit;

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
        }
        else if (view == buttonUpload) {
            firebaseDatabaseStorage.uploadFile();
        }
    }







    /*
    //REF: https://theengineerscafe.com/firebase-storage-android-tutorial/

    private static final int SELECT_PHOTO = 100;
    FirebaseStorage storage;
    StorageReference storageRef, imageRef;
    ProgressDialog progressDialog;
    UploadTask uploadTask;
    ImageView img_view;



    EditText edit_description;
    private Button btn_get_txt;
    private TextView txt_get_txt;
    Button btn_submit;
    DatabaseReference rootRef, demoRef;


    public Uri selectedImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        img_view = (ImageView) findViewById(R.id.img_view);

        edit_description = (EditText) findViewById(R.id.edit_description);
        txt_get_txt = (TextView) findViewById(R.id.txt_get_txt);
        btn_submit = (Button) findViewById(R.id.btn_submit);
        btn_get_txt = (Button) findViewById(R.id.btn_get_txt);


        //REF: https://theengineerscafe.com/save-and-retrieve-data-firebase-android/
        //accessing the firebase storage
        storage = FirebaseStorage.getInstance();
        //creates a storage reference
        storageRef = storage.getReference();

        //ref pointing to root
        rootRef = FirebaseDatabase.getInstance().getReference();

        demoRef = rootRef.child("demo");

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String value = edit_description.getText().toString();
                // Chose one or the other:

                //creates a unique id in database
                demoRef.push().setValue(value);

                //creates one value, which is easy to fetch
                demoRef.child("value").setValue(value);
            }
        });


        btn_get_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                demoRef.child("value").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String value = dataSnapshot.getValue(String.class);
                        txt_get_txt.setText(value);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        });

    }

    //REF: https://theengineerscafe.com/firebase-storage-android-tutorial/
    public void selectImage(View view) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image");
        startActivityForResult(photoPickerIntent, SELECT_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case SELECT_PHOTO:
                if (resultCode == RESULT_OK) {
                    Toast.makeText(UploadActivity.this, "Image selected, click on upload button", Toast.LENGTH_SHORT).show();
                    selectedImage = imageReturnedIntent.getData();
                }
        }
    }


    //REF: https://theengineerscafe.com/firebase-storage-android-tutorial/
    public void uploadImage(View view) {
        //create reference to images folder and adding a name to the file that will be uploaded
        imageRef = storageRef.child("images/" + selectedImage.getLastPathSegment());

        //creating and showing progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMax(100);
        progressDialog.setMessage("Uploading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.show();
        progressDialog.setCancelable(false);

        //starting upload
        uploadTask = imageRef.putFile(selectedImage);

        // Observe state change events such as progress, pause, and resume
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {

            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                //sets and increments value of progressbar
                progressDialog.incrementProgressBy((int) progress);
            }
        });

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(UploadActivity.this, "Error in uploading to firebase storage", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                Toast.makeText(UploadActivity.this, "Upload successful to firebase storage", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                //showing the uploaded image in ImageView using the download url
                Picasso.with(UploadActivity.this).load(downloadUrl).into(img_view);
            }
        });
    }
*/



}