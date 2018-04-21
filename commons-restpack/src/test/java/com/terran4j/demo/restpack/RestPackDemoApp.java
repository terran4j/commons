package com.terran4j.demo.restpack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.terran4j.commons.restpack.EnableRestPack;

@EnableRestPack
@SpringBootApplication
public class RestPackDemoApp {

    private static final Logger log = LoggerFactory.getLogger(RestPackDemoApp.class);

    public static void main(String[] args) {
        if (log.isInfoEnabled()) {
            log.info("Starting RestPackDemoApp...");
        }
        SpringApplication.run(RestPackDemoApp.class, args);
    }

}
