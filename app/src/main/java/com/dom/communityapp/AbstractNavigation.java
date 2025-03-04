package com.dom.communityapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public abstract class AbstractNavigation extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private ActionBarDrawerToggle mToggle;
    private String logTag = "ABSTRACT_NAVN";


    protected abstract DrawerLayout getdrawerLayout();

    protected abstract int getLayoutid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutid());
        DrawerLayout drawer = getdrawerLayout();


        mToggle = new ActionBarDrawerToggle(this,drawer, R.string.open,R.string.close);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawer.addDrawerListener(mToggle);
        mToggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.draw_view);
        navigationView.setNavigationItemSelectedListener(this);

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) getdrawerLayout(); /*findViewById(R.id.createeventactivity);*/
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

//          This creates a simple second navigation menu i the top right corner.
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.navigation, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mToggle.onOptionsItemSelected(item)){
            return true;
        }

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.action_map) {
            // Handle the camera action
            Log.d(logTag, "OPEN MAP");
            Intent intent = new Intent(this, MapsActivity.class);
            startActivity(intent);
            finish();

        } else if (id == R.id.action_settings ) {
            Log.d(logTag, "OPEN Setting");
            Intent intent = new Intent(this, SettingActivity.class);
            startActivity(intent);
            finish();
            ;

        } else if (id == R.id.action_create) {
            Log.d(logTag, "OPEN CREATE");
            Intent intent = new Intent(this, CreateEventActivity.class);
            startActivity(intent);
            finish();
        }



        DrawerLayout drawer = (DrawerLayout) getdrawerLayout();
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
