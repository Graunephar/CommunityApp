package com.dom.communityapp;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

/** Activity for personalization of the app, not all features are fully
 * implemented due to timepressure and prioritazation
 *
*/

public class SettingActivity extends AbstractNavigation {

    SeekBar seekBar_id;
    TextView seekBarValue;


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

    seekBar_id = (SeekBar) findViewById(R.id.SB_seekBar);
    seekBarValue = (TextView) findViewById(R.id.txt_seekbar_value);


  /*
  Future work: bind the seekbar progress to the textview
  seekBar_id.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            onStartTrackingTouch(seekBar);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            seekBarValue.setText(seekBar.getProgress());
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            seekBarValue.setText(seekBar.getProgress());
        }

    });*/

    }
}
