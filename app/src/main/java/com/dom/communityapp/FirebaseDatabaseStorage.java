package com.dom.communityapp;


import android.app.ProgressDialog;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;


/**
 * Created by mrl on 07/12/2017.
 */

public class FirebaseDatabaseStorage {
    UploadActivity uploadActivity;
    private UploadTask uploadTask;

    public FirebaseDatabaseStorage(UploadActivity uploadActivity) {
        this.uploadActivity = uploadActivity;
    }

    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();


    public void uploadFile() {
        //REF: https://www.simplifiedcoding.net/firebase-storage-tutorial-android/
        //REF: https://theengineerscafe.com/firebase-storage-android-tutorial/
        if (uploadActivity.filePath != null) {

            final ProgressDialog progressDialog = new ProgressDialog(uploadActivity);
            progressDialog.setMax(100);
            progressDialog.setMessage("Uploading...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.show();
            progressDialog.setCancelable(false);

            StorageReference imageRef = storageReference.child("images/" + uploadActivity.filePath.getLastPathSegment());

            uploadTask = imageRef.putFile(uploadActivity.filePath);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    //showing the uploaded image in ImageView using the download url. Choose this method or local method.
                    Picasso.with(uploadActivity).load(downloadUrl).into(uploadActivity.img_view);

                    progressDialog.dismiss();
                    Toast.makeText(uploadActivity, "File Uploaded ", Toast.LENGTH_LONG).show();
                }
            });
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    progressDialog.dismiss();
                    Toast.makeText(uploadActivity, exception.getMessage(), Toast.LENGTH_LONG).show();
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
*/