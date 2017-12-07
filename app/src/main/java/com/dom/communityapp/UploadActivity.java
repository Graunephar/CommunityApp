package com.dom.communityapp;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class UploadActivity extends AppCompatActivity {

    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);




        // Firebase reference to Cloud Storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference mStorageRef = storage.getReferenceFromUrl("gs://communityapp-649ff.appspot.com").child("ic_launcher.png");

//        mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://communityapp-649ff.appspot.com").child("gradle_trouble.png");




    }



    private void uploadFiletoFirebase(){
        // Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.gradle_trouble);

        Uri file = Uri.fromFile(new File("WhatsApp/Media/WhatsApp Images/Sent/IMG-20171106-WA0000.jpg"));
        StorageReference testRef = mStorageRef.child("images/test.jpg");

        testRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // get URL of uploaded file
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Error uploading to Firebase cloud storage " + e);
                    }
                });
    }


    private void uploadManager(){
        AssetManager assetManager = UploadActivity.this.getAssets();
        InputStream inputStream;
        Bitmap bitmap;
        try {
            //get bitmap from asset folder
            inputStream = assetManager.open("gradle_trouble.png");
            bitmap = BitmapFactory.decodeStream(inputStream);

            //decode output to bytes
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            byte[] data = outputStream.toByteArray();

            //upload to firebase
            UploadTask uploadTask = mStorageRef.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    System.out.println("Error uploading to Firebase cloud storage " + e);
                    Toast.makeText(UploadActivity.this, "Upload Failed", Toast.LENGTH_SHORT).show();

                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(UploadActivity.this, "Upload Succeeded", Toast.LENGTH_SHORT).show();

                }
            });


        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
