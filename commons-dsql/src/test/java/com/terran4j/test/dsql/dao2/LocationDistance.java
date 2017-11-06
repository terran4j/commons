package com.terran4j.test.dsql.dao2;

import com.terran4j.commons.util.Strings;
import com.terran4j.test.dsql.dao.Location;

public class LocationDistance  {

    private Location location;

    private Long distance;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Long getDistance() {
        return distance;
    }

    public void setDistance(Long distance) {
        this.distance = distance;
    }

    public String toString() {
        return Strings.toString(this);
    }

}
