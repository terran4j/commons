package com.terran4j.test.dsql.dao4;

import com.terran4j.commons.dsql.DsqlRepository;
import com.terran4j.test.dsql.dao.Location;
import com.terran4j.test.dsql.dao.LocationQuery;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LocationAutoDAO extends DsqlRepository<Location> {

    List<Location> getLocations(LocationQuery query);

    int countLocation(LocationQuery query);

    Location getNearestLocation(@Param("lat") Double lat, @Param("lon") Double lon);
}
