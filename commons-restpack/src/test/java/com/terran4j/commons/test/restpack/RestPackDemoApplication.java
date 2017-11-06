package com.terran4j.commons.test.restpack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.terran4j.commons.restpack.EnableRestPack;

@EnableRestPack
@SpringBootApplication
public class RestPackDemoApplication {

    private static final Logger log = LoggerFactory.getLogger(RestPackDemoApplication.class);

    public static void main(String[] args) {
        if (log.isInfoEnabled()) {
            log.info("Starting RestPackDemoApplication...");
        }
        SpringApplication.run(RestPackDemoApplication.class, args);
    }

}
