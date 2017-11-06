package com.terran4j.test.dsql.dao3;

import com.terran4j.commons.dsql.DsqlRepository;
import com.terran4j.commons.dsql.Query;
import com.terran4j.test.dsql.dao.LocationQuery;

import java.util.List;

public interface DistancedLocationDAO extends DsqlRepository<DistancedLocation> {

    @Query("locations")
    List<DistancedLocation> query(LocationQuery query);

    @Query("location-count")
    int count(LocationQuery query);

}
