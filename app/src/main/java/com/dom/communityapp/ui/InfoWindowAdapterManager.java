package com.dom.communityapp.ui;

import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.util.HashMap;

/**
 * Created by mrl on 13/12/2017.
 */

public class InfoWindowAdapterManager implements GoogleMap.InfoWindowAdapter{
    private HashMap<String, InfoWindowAdapter> mAdapters;

    public InfoWindowAdapterManager() {
        this.mAdapters = new HashMap<>();
    }

    @Override
    public View getInfoWindow(Marker marker) {
        InfoWindowAdapter infoWindowAdapter = mAdapters.get(marker.getId());
        return infoWindowAdapter.getInfoWindow(marker);
    }

    @Override
    public View getInfoContents(Marker marker) {
        InfoWindowAdapter infoWindowAdapter = mAdapters.get(marker.getId());
        return infoWindowAdapter.getInfoContents(marker);
    }

    public void addAdapter(Marker key, InfoWindowAdapter adapter){
        mAdapters.put(key.getId(), adapter);
    }

    public void removeAdapter(Marker key, InfoWindowAdapter adapter){
        mAdapters.remove(key.getId());
    }
}
