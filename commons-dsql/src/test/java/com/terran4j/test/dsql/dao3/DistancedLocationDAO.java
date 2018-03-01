package com.terran4j.test.dsql.dao3;

import com.terran4j.commons.dsql.DsqlQuery;
import com.terran4j.commons.dsql.DsqlRepository;
import com.terran4j.test.dsql.dao.LocationQuery;

import java.util.List;

public interface DistancedLocationDAO extends DsqlRepository<DistancedLocation> {

    @DsqlQuery("locations")
    List<DistancedLocation> query(LocationQuery query);

    @DsqlQuery("location-count")
    int count(LocationQuery query);

}
