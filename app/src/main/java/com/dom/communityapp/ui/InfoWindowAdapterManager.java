package com.dom.communityapp.ui;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.dom.communityapp.models.CommunityIssue;
import com.dom.communityapp.storage.IssueResolver;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by mrl on 13/12/2017.
 */

public class InfoWindowAdapterManager implements GoogleMap.InfoWindowAdapter, Serializable {
    private transient Activity mActivity;
    private final HashMap<CommunityIssue, Marker> mIssueAdapterReference;
    private transient IssueResolver mIssueResolver;
    private HashMap<Marker, InfoWindowAdapter> mAdapters;

    public InfoWindowAdapterManager(Activity activity, IssueResolver resolver) {
        this.mAdapters = new HashMap<>();
        this.mActivity = activity;
        this.mIssueAdapterReference = new HashMap<>();
        this.mIssueResolver = resolver;
    }

    public Activity getmActivity() {
        return mActivity;
    }

    public void setmActivity(Activity mActivity) {
        this.mActivity = mActivity;
    }

    public IssueResolver getmIssueResolver() {
        return mIssueResolver;
    }

    public void setmIssueResolver(IssueResolver mIssueResolver) {
        this.mIssueResolver = mIssueResolver;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        InfoWindowAdapter infoWindowAdapter = mAdapters.get(marker);
        return infoWindowAdapter.getInfoWindow(marker);
    }

    @Override
    public View getInfoContents(Marker marker) {
        InfoWindowAdapter infoWindowAdapter = mAdapters.get(marker);
        return infoWindowAdapter.getInfoContents(marker);
    }

    public void addAdapter(Marker key, CommunityIssue issue) {
        InfoWindowAdapter adapter = new InfoWindowAdapter(mActivity, issue, mIssueResolver);
        mIssueAdapterReference.put(issue, key);
        mAdapters.put(key, adapter);
    }

    public void removeAdapterByIssue(CommunityIssue issue) {
        Marker marker = mIssueAdapterReference.get(issue);
        mAdapters.remove(marker);
        mIssueAdapterReference.remove(issue);
        marker.remove();
    }

    public void changeMarker(CommunityIssue issue, Marker newmarker) {
        Marker oldmarker = mIssueAdapterReference.get(issue);
        mIssueAdapterReference.remove(issue);
        mIssueAdapterReference.put(issue, newmarker);
        InfoWindowAdapter infowindowadapter = mAdapters.get(oldmarker);
        mAdapters.remove(oldmarker);
        mAdapters.put(newmarker, infowindowadapter);

        oldmarker.remove();
    }

    public void clickedInfoWindow(Marker marker) {
        InfoWindowAdapter clickedadapter = mAdapters.get(marker);
        clickedadapter.showDetailsFragment();
    }
}
