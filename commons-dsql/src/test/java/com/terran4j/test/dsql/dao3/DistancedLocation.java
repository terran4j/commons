package com.terran4j.test.dsql.dao3;

import com.terran4j.test.dsql.dao.Location;

public class DistancedLocation extends Location {

    private Long distance;

    public Long getDistance() {
        return distance;
    }

    public void setDistance(Long distance) {
        this.distance = distance;
    }

}
