package com.terran4j.demo.dsql.appjpa;

import com.terran4j.commons.test.DatabaseTestConfig;
import com.terran4j.commons.util.Strings;
import com.terran4j.demo.dsql.Address;
import com.terran4j.demo.dsql.AddressDAO;
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
@Import(DatabaseTestConfig.class) // 自动装配默认的数据库配置。
@SpringBootApplication
public class JpaDemoApplication implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(JpaDemoApplication.class);

    @Autowired
    private AddressDAO addressDAO;

    @Override
    public void run(ApplicationArguments appArgs) throws Exception {
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

        List<Address> result = addressDAO.findAll();
        if (log.isInfoEnabled()) {
            log.info("\n查询结果：{}", Strings.toString(result));
        }
    }

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(JpaDemoApplication.class);
        app.setWebEnvironment(false);
        app.run(args);
        System.exit(0);
    }
}
