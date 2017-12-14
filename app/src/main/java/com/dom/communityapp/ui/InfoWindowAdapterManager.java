package com.dom.communityapp.ui;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.dom.communityapp.models.CommunityIssue;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by mrl on 13/12/2017.
 */

public class InfoWindowAdapterManager implements GoogleMap.InfoWindowAdapter {
    private final Activity mActivity;
    private final HashMap<CommunityIssue, Marker> mIssueAdapterReference;
    private BiMap<Marker, InfoWindowAdapter> mAdapters;

    public InfoWindowAdapterManager(Activity activity) {
        this.mAdapters = HashBiMap.create();
        this.mActivity = activity;
        this.mIssueAdapterReference = new HashMap<>();
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
        InfoWindowAdapter adapter = new InfoWindowAdapter(mActivity, issue);
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
}
