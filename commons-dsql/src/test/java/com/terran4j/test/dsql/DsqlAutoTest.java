package com.terran4j.test.dsql;

import com.terran4j.test.dsql.dao.Location;
import com.terran4j.test.dsql.dao.LocationQuery;
import com.terran4j.test.dsql.dao2.LocationDistance;
import com.terran4j.test.dsql.dao2.LocationDistanceDAO;
import com.terran4j.test.dsql.dao3.DistancedLocation;
import com.terran4j.test.dsql.dao3.DistancedLocationDAO;
import com.terran4j.test.dsql.dao4.LocationAutoDAO;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class DsqlAutoTest extends BaseDsqlTest {

    private static final Logger log = LoggerFactory.getLogger(DsqlAutoTest.class);

    @Autowired
    protected LocationAutoDAO locationAutoDAO;

    @Test
    public void testGetLocations() throws Exception {
        LocationQuery query = new LocationQuery();
        query.setLon(loc3.getLon());
        query.setLat(loc3.getLat());
        List<Location> results = locationAutoDAO.getLocations(query);
        log.info("results:\n{}", results);
        Assert.assertEquals(2, results.size());
        Assert.assertEquals(loc1.getName(), results.get(0).getName());
        Assert.assertEquals(loc2.getName(), results.get(1).getName());
    }

    @Test
    public void testCountLocation() throws Exception {
        LocationQuery query = new LocationQuery();
        query.setLon(loc3.getLon());
        query.setLat(loc3.getLat());
        int size = locationAutoDAO.countLocation(query);
        Assert.assertEquals(1, size);
    }

    @Test
    public void testGetNearestLocation() throws Exception {
        Location location = locationAutoDAO.getNearestLocation(loc1.getLat(), loc1.getLon());
        Assert.assertNotNull(location);
        Assert.assertEquals(loc1.getName(), location.getName());
    }

}
