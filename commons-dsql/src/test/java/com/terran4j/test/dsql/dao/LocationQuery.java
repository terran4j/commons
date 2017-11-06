package com.terran4j.test.dsql.dao;

public class LocationQuery {

    private Double lat;

    private Double lon;

    private boolean nearFirst = true;

    public boolean isNearFirst() {
        return nearFirst;
    }

    public void setNearFirst(boolean nearFirst) {
        this.nearFirst = nearFirst;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }
}
