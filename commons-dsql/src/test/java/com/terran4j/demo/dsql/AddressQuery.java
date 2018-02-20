package com.terran4j.demo.dsql;

import com.terran4j.commons.util.Strings;

public class AddressQuery {

    private Double lat;

    private Double lon;

    private String name;

    private boolean nearFirst = true;

    public AddressQuery(Double lat, Double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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

    public final String toString() {
        return Strings.toString(this);
    }

}
