package com.terran4j.test.dsql;

import com.terran4j.commons.dsql.DsqlExecutor;
import com.terran4j.commons.dsql.EnableDsqlRepositories;
import com.terran4j.commons.dsql.config.DsqlConfiguration;
import com.terran4j.commons.dsql.impl.DsqlExecutorImpl;
import com.terran4j.commons.test.*;
import com.terran4j.test.dsql.dao.Location;
import com.terran4j.test.dsql.dao.LocationDAO;
import com.terran4j.test.dsql.dao1.LocationDsqlDAO;
import com.terran4j.test.dsql.dao2.LocationDistanceDAO;
import com.terran4j.test.dsql.dao3.DistancedLocationDAO;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestExecutionListeners;

@TruncateTable(basePackageClass = Location.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = {
        BaseDsqlTest.Application.class
})
@TestExecutionListeners({DatabaseInitializer.class})
public abstract class BaseDsqlTest extends BaseSpringBootTest {

    @EntityScan(basePackageClasses = Location.class)
    @EnableJpaRepositories(basePackageClasses = LocationDAO.class)
    @EnableDsqlRepositories(basePackageClasses = {
            LocationDsqlDAO.class,
            LocationDistanceDAO.class,
            DistancedLocationDAO.class,
    })
    @Import({
            DsqlConfiguration.class,
            DatabaseTestConfig.class,
    })
    @SpringBootApplication
    public static class Application {
    }

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @Autowired
    protected LocationDAO locationDAO;

    protected DsqlExecutor dsqlExecutor;

    protected Location loc1 = new Location("金域国际中心",
            116.3139456511, 40.0676693732);

    protected Location loc2 = new Location("融泽嘉园一号院南门",
            116.3086509705, 40.0668729389);

    Location loc3 = new Location("龙泽地铁站",
            116.3193368912, 40.0707811250);

    @Before
    public void setUp() {
        locationDAO.save(loc1);
        locationDAO.save(loc2);
        DsqlExecutorImpl executor = new DsqlExecutorImpl(jdbcTemplate);
        this.dsqlExecutor = executor;

    }


}
