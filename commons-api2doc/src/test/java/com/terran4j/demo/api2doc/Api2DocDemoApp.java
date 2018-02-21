package com.terran4j.demo.api2doc;

import com.terran4j.commons.api2doc.config.EnableApi2Doc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableApi2Doc
@SpringBootApplication
public class Api2DocDemoApp {

    public static void main(String[] args) {
        SpringApplication.run(Api2DocDemoApp.class, args);
    }

}