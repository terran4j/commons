package com.terran4j.demo.dsql;

import com.terran4j.commons.dsql.DsqlRepository;
import com.terran4j.commons.dsql.Modifying;
import com.terran4j.commons.dsql.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AddressDistanceDAO extends DsqlRepository<AddressDistance> {
    
    @Query("address-nearest")
    AddressDistance getNearest(@Param("lat") double lat, @Param("lon") double lon);

    @Query("address-nearest-2")
    AddressDistance getNearest2(double lat, double lon);

    @Query("address-list")
    List<AddressDistance> getAll(AddressQuery params);

    @Query("address-count")
    int count(@Param("lat") double lat, @Param("lon") double lon,
              @Param("maxDistance") int maxDistance);

    @Modifying("address-update-nearest")
    int updateNearest(@Param("name") String name,
                      @Param("lat") double lat, @Param("lon") double lon);

    @Modifying("address-delete-nearest")
    int deleteNearest(@Param("lat") double lat, @Param("lon") double lon);
}