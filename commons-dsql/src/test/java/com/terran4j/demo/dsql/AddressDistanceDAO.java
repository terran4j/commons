package com.terran4j.demo.dsql;

import com.terran4j.commons.dsql.DsqlRepository;
import com.terran4j.commons.dsql.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AddressDistanceDAO extends DsqlRepository<AddressDistance> {

    @Query("addresses")
    List<AddressDistance> getAll(AddressQuery params);

    @Query("nearest-address")
    AddressDistance getNearest(
            @Param("lat") double lat, @Param("lon") double lon);

    @Query("nearest-address-2")
    AddressDistance getNearest2(double lat, double lon);

}