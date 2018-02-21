package com.terran4j.test.dsql;

import com.terran4j.test.dsql.dao1.LocationDsqlDAO;
import com.terran4j.test.dsql.dao2.LocationDistance;
import com.terran4j.test.dsql.dao2.LocationDistanceDAO;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class DsqlModifyingTest extends BaseDsqlTest {

    private static final Logger log = LoggerFactory.getLogger(DsqlModifyingTest.class);

    @Autowired
    private LocationDsqlDAO locationDsqlDAO;

    @Autowired
    private LocationDistanceDAO locationDistanceDAO;

    @Test
    public void testUpdate() throws Exception {
        log.info("testUpdate");
        LocationDistance nearest = locationDistanceDAO.getNearest(
                loc3.getLat(), loc3.getLon());
        Assert.assertEquals("金域国际中心", nearest.getLocation().getName());

        String newName = "最近位置";
        int rowCount = locationDsqlDAO.updateNearest(newName, loc3.getLat(), loc3.getLon());
        Assert.assertEquals(1, rowCount);

        nearest = locationDistanceDAO.getNearest(
                loc3.getLat(), loc3.getLon());
        Assert.assertEquals(newName, nearest.getLocation().getName());
    }

    @Test
    public void testDelete() throws Exception {
        log.info("testDelete");
        LocationDistance nearest = locationDistanceDAO.getNearest(
                loc3.getLat(), loc3.getLon());
        Assert.assertEquals(loc1.getName(), nearest.getLocation().getName());

        int rowCount = locationDsqlDAO.deleteNearest(loc3.getLat(), loc3.getLon());
        Assert.assertEquals(1, rowCount);

        nearest = locationDistanceDAO.getNearest(
                loc3.getLat(), loc3.getLon());
        Assert.assertEquals(loc2.getName(), nearest.getLocation().getName());
    }
}
