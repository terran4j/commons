package com.terran4j.test.dsql;

import com.terran4j.test.dsql.dao.*;
import com.terran4j.test.dsql.dao2.LocationDistance;
import com.terran4j.test.dsql.dao2.LocationDistanceDAO;
import com.terran4j.test.dsql.dao3.DistancedLocation;
import com.terran4j.test.dsql.dao3.DistancedLocationDAO;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class DsqlRepositoryTest extends BaseDsqlTest {

    private static final Logger log = LoggerFactory.getLogger(DsqlRepositoryTest.class);

    @Autowired
    private LocationDistanceDAO locationDistanceDAO;

    @Autowired
    private DistancedLocationDAO distancedLocationDAO;

    @Test
    public void testQueryWithCompositeEntity() throws Exception {
        LocationQuery query = new LocationQuery();
        query.setLon(loc3.getLon());
        query.setLat(loc3.getLat());
        List<LocationDistance> results = locationDistanceDAO.query(query);
        if (log.isInfoEnabled()) {
            log.info("results:\n{}", results);
        }
        Assert.assertEquals(2, results.size());
        Assert.assertEquals(loc1.getName(), results.get(0).getLocation().getName());
        Assert.assertEquals(new Long(575), results.get(0).getDistance());
        Assert.assertEquals(loc2.getName(), results.get(1).getLocation().getName());
        Assert.assertEquals(new Long(1009), results.get(1).getDistance());
    }

    @Test
    public void testQueryWithExtendEntity() throws Exception {
        LocationQuery query = new LocationQuery();
        query.setLon(loc3.getLon());
        query.setLat(loc3.getLat());
        List<DistancedLocation> results = distancedLocationDAO.query(query);
        if (log.isInfoEnabled()) {
            log.info("results:\n{}", results);
        }
        Assert.assertEquals(2, results.size());
        Assert.assertEquals(loc1.getName(), results.get(0).getName());
        Assert.assertEquals(new Long(575), results.get(0).getDistance());
        Assert.assertEquals(loc2.getName(), results.get(1).getName());
        Assert.assertEquals(new Long(1009), results.get(1).getDistance());
    }

    @Test
    public void testQueryWithParamAnno() throws Exception {
        LocationDistance nearestLocation = locationDistanceDAO.getNearest(
                loc3.getLat(), loc3.getLon());
        if (log.isInfoEnabled()) {
            log.info("nearestLocation:\n{}", nearestLocation);
        }
        Assert.assertEquals(loc1.getName(), nearestLocation.getLocation().getName());
        Assert.assertEquals(new Long(575), nearestLocation.getDistance());
    }

    @Test
    public void testCount() throws Exception {
        LocationQuery query = new LocationQuery();
        query.setLon(loc3.getLon());
        query.setLat(loc3.getLat());
        int size = distancedLocationDAO.count(query);
        Assert.assertEquals(1, size);
    }
}
