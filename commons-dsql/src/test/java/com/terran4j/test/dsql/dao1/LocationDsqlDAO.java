package com.terran4j.test.dsql.dao1;

import com.terran4j.commons.dsql.DsqlRepository;
import com.terran4j.commons.dsql.Modifying;
import com.terran4j.commons.dsql.Query;
import com.terran4j.test.dsql.dao.Location;
import org.springframework.data.repository.query.Param;

public interface LocationDsqlDAO extends DsqlRepository<Location> {

    @Modifying("update-nearest")
    int updateNearest(@Param("name") String name,
                      @Param("lat") double lat, @Param("lon") double lon);

    @Modifying("delete-nearest")
    int deleteNearest(@Param("lat") double lat, @Param("lon") double lon);
}
