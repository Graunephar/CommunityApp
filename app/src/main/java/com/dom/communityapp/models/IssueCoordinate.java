package com.dom.communityapp.models;

import java.io.Serializable;

/**
 * Created by daniel on 12/16/17.
 */

public class IssueCoordinate implements Serializable{

    private Double latitude;
    private Double longitude;

    public IssueCoordinate(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }


}
