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

import com.google.android.gms.maps.MapView;


/* This activity is a temporary copy of create event activity, made for
testing integration with firebase without getting merge conflicts in git */


public class FirebaseCreateEventTestTempActivity extends AppCompatActivity {

//Local variable

    Bitmap myImage;
    ImageView viewer;
    EditText short_description, long_description;
    Spinner tag_spin, cat_spin, time_spin;
    MapView map_show;

    //Request codes
    private static final int CAMERA_REQUEST_CODE = 11;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.createeventactivity);
        mToggle = new ActionBarDrawerToggle(this,mDrawerLayout, R.string.open,R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewer = (ImageView) findViewById(R.id.imageview_event);
        short_description = findViewById(R.id.edittext_short_description);
        long_description = findViewById(R.id.edittext_long_description);
        tag_spin = findViewById(R.id.spinner_tags);
        cat_spin = findViewById(R.id.spinner_categories);
        time_spin = findViewById(R.id.spinner_time_required);
        map_show = findViewById(R.id.mapView_create_event);

        viewer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                startActivityForResult(intent, CAMERA_REQUEST_CODE);
                intent.putExtra("return-data", true);
            }


        });

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

    public void createEvent(){

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(mToggle.onOptionsItemSelected(item)){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
