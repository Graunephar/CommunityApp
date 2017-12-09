package com.dom.communityapp;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.content.Intent;
import android.provider.MediaStore;
import android.widget.Spinner;

import com.google.android.gms.maps.MapView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


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

    @OnClick(R.id.imageView_Event)
    public void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        startActivityForResult(intent, CAMERA_REQUEST_CODE);
        intent.putExtra("return-data", true);
    }

}
