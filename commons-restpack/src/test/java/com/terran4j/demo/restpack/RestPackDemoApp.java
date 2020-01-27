package com.terran4j.demo.restpack;

import com.terran4j.commons.restpack.EnableRestPack;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@EnableRestPack
@SpringBootApplication
public class RestPackDemoApp {

    public static void main(String[] args) {
        if (log.isInfoEnabled()) {
            log.info("Starting RestPackDemoApp...");
        }
        SpringApplication.run(RestPackDemoApp.class, args);
    }

}
