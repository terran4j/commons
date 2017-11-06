package com.terran4j.test.dsql.dao2;

import com.terran4j.commons.dsql.DsqlRepository;
import com.terran4j.commons.dsql.Query;
import com.terran4j.test.dsql.dao.LocationQuery;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LocationDistanceDAO extends DsqlRepository<LocationDistance> {

    @Query("locations")
    List<LocationDistance> query(LocationQuery query);

    @Query("location-nearest")
    LocationDistance getNearest(@Param("lat") double lat, @Param("lon") double lon);

}
