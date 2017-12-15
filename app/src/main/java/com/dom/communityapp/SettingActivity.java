package com.dom.communityapp;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SettingActivity extends AbstractNavigation {

    @Override
    protected DrawerLayout getdrawerLayout() {
        return (DrawerLayout) findViewById(R.id.SettingActivity);
    }

    @Override
    protected int getLayoutid() {
        return R.layout.activity_setting;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_setting);
    }
}
