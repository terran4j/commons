package com.terran4j.demo.dsql;

import com.terran4j.commons.dsql.EnableDsqlRepositories;
import com.terran4j.commons.test.DatabaseTestConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.ArrayList;
import java.util.List;

@EntityScan(basePackageClasses = Address.class)
@EnableJpaRepositories(basePackageClasses = AddressDAO.class)
@EnableDsqlRepositories(basePackageClasses = AddressDistanceDAO.class)
@Import(DatabaseTestConfig.class) // 自动装配默认的数据库配置。
@SpringBootApplication
public class DsqlDemoApplication implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DsqlDemoApplication.class);

    @Autowired
    private AddressDAO addressDAO;

    @Autowired
    private AddressDistanceDAO addressDistanceDAO;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 清空表中的数据，以避免旧数据干扰运行。
        addressDAO.deleteAll();

        // 添加几条位置数据，以方便下面的查询。
        List<Address> addresses = new ArrayList<>();
        Address address1 = new Address("金域国际中心",
                116.3139456511, 40.0676693732);
        addresses.add(address1);
        Address address2 = new Address("龙泽地铁站",
                116.3193368912, 40.0707811250);
        addresses.add(address2);
        Address address3 = new Address("回龙观地铁站",
                116.3362830877, 40.0707770199);
        addresses.add(address3);
        addressDAO.save(addresses);

        // 当前位置，作为查询的参数
        Address currentAddress = new Address("融泽嘉园一号院",
                116.3086509705, 40.0668729389);
        AddressQuery params = new AddressQuery(
                currentAddress.getLat(), currentAddress.getLon());
        params.setName("%地铁%");

        params.setNearFirst(true);
        List<AddressDistance> result = addressDistanceDAO.getAll(params);
        if (log.isInfoEnabled()) {
            log.info("\n由近及远查询所有，入参： {}, \n查询结果： {}", params, result);
        }

        params.setNearFirst(false);
        result = addressDistanceDAO.getAll(params);
        if (log.isInfoEnabled()) {
            log.info("\n由远及近查询所有，入参： {}, \n查询结果： {}", params, result);
        }

        AddressDistance addressDistance = addressDistanceDAO.getNearest(
                currentAddress.getLat(), currentAddress.getLon());
        if (log.isInfoEnabled()) {
            log.info("\n查询最近位置（指定参数名），当前位置：{},\n最近位置： {}",
                    currentAddress, addressDistance);
        }

        addressDistance = addressDistanceDAO.getNearest2(
                currentAddress.getLat(), currentAddress.getLon());
        if (log.isInfoEnabled()) {
            log.info("\n查询最近位置（不指定参数名），当前位置：{},\n最近位置： {}",
                    currentAddress, addressDistance);
        }
    }

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(DsqlDemoApplication.class);
        app.setWebEnvironment(false);
        app.run(args);
        System.exit(0);
    }
}
