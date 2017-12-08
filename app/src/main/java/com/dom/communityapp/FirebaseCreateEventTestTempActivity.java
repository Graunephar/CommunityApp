package com.dom.communityapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.dom.communityapp.models.CommunityIssue;
import com.dom.communityapp.storage.FirebaseDatabaseStorage;
import com.dom.communityapp.storage.FirebaseObserver;
import com.google.android.gms.maps.MapView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/* This activity is a temporary copy of create event activity, made for
testing integration with firebase without getting merge conflicts in git */


public class FirebaseCreateEventTestTempActivity extends AppCompatActivity {

//Local variable

    Bitmap myImage;
    ImageView viewer;
    MapView map_show;

    //Request codes
    private static final int CAMERA_REQUEST_CODE = 11;
    private FirebaseDatabaseStorage mStorage = new FirebaseDatabaseStorage(this);

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;

    @BindView(R.id.edittext_short_description2) EditText short_description;
    @BindView(R.id.edittext_long_description2) EditText long_description;
    @BindView(R.id.spinner_tags2) Spinner tag_spin;
    @BindView(R.id.spinner_categories2) Spinner cat_spin;
    @BindView(R.id.spinner_time_required2) Spinner time_spin;



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

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            Bundle bundle1 = data.getExtras();

            myImage = (Bitmap) bundle1.get("data");
            viewer.setImageBitmap(myImage);
        }
    }

    @OnClick(R.id.imageview_event2)
    public void takePhoto(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        startActivityForResult(intent, CAMERA_REQUEST_CODE);
        intent.putExtra("return-data", true);
    }

    @OnClick(R.id.create_event_button_OK2)
    public void createEvent(){

        String sshort = short_description.getText().toString();
        String llong = long_description.getText().toString();
        String cat_text = cat_spin.getSelectedItem().toString();
        String tag_text = tag_spin.getSelectedItem().toString();
        String time_text = time_spin.getSelectedItem().toString();

        CommunityIssue issue = new CommunityIssue(sshort, llong, cat_text, tag_text, time_text);

        mStorage.saveIssueToDatabase(issue);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(mToggle.onOptionsItemSelected(item)){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
