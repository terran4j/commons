package com.terran4j.demo.dsql;

import com.terran4j.commons.dsql.DsqlModifying;
import com.terran4j.commons.dsql.DsqlQuery;
import com.terran4j.commons.dsql.DsqlRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AddressDistanceDAO extends DsqlRepository<AddressDistance> {
    
    @DsqlQuery("address-nearest")
    AddressDistance getNearest(@Param("lat") double lat, @Param("lon") double lon);

    @DsqlQuery("address-nearest-2")
    AddressDistance getNearest2(double lat, double lon);

    @DsqlQuery("address-list")
    List<AddressDistance> getAll(AddressQuery params);

    @DsqlQuery("address-count")
    int count(@Param("lat") double lat, @Param("lon") double lon,
              @Param("maxDistance") int maxDistance);

    @DsqlModifying("address-update-nearest")
    int updateNearest(@Param("name") String name,
                      @Param("lat") double lat, @Param("lon") double lon);

    @DsqlModifying("address-delete-nearest")
    int deleteNearest(@Param("lat") double lat, @Param("lon") double lon);
}