package com.dom.communityapp.ui;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dom.communityapp.R;
import com.dom.communityapp.models.CommunityIssue;
import com.dom.communityapp.storage.IssueResolver;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.io.Serializable;

/**
 * Created by daniel on 12/10/17.
 */

public class InfoWindowAdapter implements GoogleMap.InfoWindowAdapter, Serializable {

    private IssueResolver mIssueResolver;
    private Activity mActivity;
    private final CommunityIssue mIssue;
    private View mView;
    private TextView markerLabel;
    private TextView anotherLabel;

    private ImageView iconView;

    public InfoWindowAdapter(Activity context, CommunityIssue issue, IssueResolver resolver) {

        this.mIssue = issue;
        this.mActivity = context;
        this.mView = mActivity.getLayoutInflater().inflate(R.layout.bubble_marker_layout, null);
        iconView = (ImageView) mView.findViewById(R.id.marker_icon);
        this.mIssueResolver = resolver;

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
        if (bitmap != null) {
            iconView.setImageBitmap(mIssue.getIssueImage().getBitmap());
        }
    }

    public CommunityIssue getIssue() {
        return mIssue;
    }

    public void showDetailsFragment(){
        FragmentManager manager = mActivity.getFragmentManager();
        DetailsFragment detailsFragment = new DetailsFragment();
        detailsFragment.addInfoWindowAdapter(this);
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.map_container,detailsFragment,"dtFragment");
        transaction.commit();
    }


    public void removeDetailsFragment() {
        FragmentManager manager = mActivity.getFragmentManager();

        DetailsFragment detailsFragment = (DetailsFragment) manager.findFragmentByTag("dtFragment");
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.remove(detailsFragment);
        transaction.commit();
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof InfoWindowAdapter)) return false;
        InfoWindowAdapter objadapter = (InfoWindowAdapter) obj;
        return this.mIssue.equals(objadapter.mIssue);
    }

    @Override
    public int hashCode() {
        return mIssue.hashCode();
    }

    public void resolveIssue(CommunityIssue issue) {
            mIssueResolver.resolve(issue);
    }

}
