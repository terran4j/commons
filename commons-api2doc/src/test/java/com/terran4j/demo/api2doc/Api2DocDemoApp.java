package com.terran4j.demo.api2doc;

import com.terran4j.commons.api2doc.config.EnableApi2Doc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//  文档访问地址： http://localhost:8080/api2doc/home.html
@EnableApi2Doc
@SpringBootApplication
public class Api2DocDemoApp {

    public static void main(String[] args) {
        SpringApplication.run(Api2DocDemoApp.class, args);
    }

}