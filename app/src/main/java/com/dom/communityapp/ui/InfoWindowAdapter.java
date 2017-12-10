package com.dom.communityapp.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dom.communityapp.R;
import com.dom.communityapp.models.CommunityIssue;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by daniel on 12/10/17.
 */

public class InfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final Activity mContext;
    private final CommunityIssue mIssue;
    private final View mView;
    private TextView markerLabel;
    private TextView anotherLabel;

    private ImageView iconView;

    public InfoWindowAdapter(Activity context, CommunityIssue issue) {

        this.mIssue = issue;
        this.mContext = context;
        this.mView = mContext.getLayoutInflater().inflate(R.layout.bubble_marker_layout, null);
        iconView = (ImageView) mView.findViewById(R.id.marker_icon);

    }


    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {

        markerLabel = (TextView) mView.findViewById(R.id.marker_label);
        anotherLabel = (TextView) mView.findViewById(R.id.another_label);

        markerLabel.setText(mIssue.getShort_description());
        anotherLabel.setText(mIssue.getLong_description());

        setIcon();

        return mView;
    }

    private void setIcon() {
        Bitmap bitmap = mIssue.getIssueImage().getBitmap();
        if(bitmap != null ) {
            iconView.setImageBitmap(mIssue.getIssueImage().getBitmap());
        }
    }
}
