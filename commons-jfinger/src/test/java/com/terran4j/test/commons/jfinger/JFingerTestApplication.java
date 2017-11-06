package com.terran4j.test.commons.jfinger;

import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;
import java.util.ResourceBundle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.terran4j.commons.jfinger.EnableJFinger;

@EnableJFinger
@SpringBootApplication
public class JFingerTestApplication {
    
    public static void main(String[] args) throws Exception {
        SpringApplication.run(JFingerTestApplication.class, args);
        
        String fileName = "error.properties";
        
        Properties props = new Properties();
        Enumeration<URL> urls = SpringApplication.class.getClassLoader() //
        		.getResources(fileName);
        if (urls != null && urls.hasMoreElements()) {
        	while (urls.hasMoreElements()) {
        		URL url = urls.nextElement();
        		InputStream in = url.openStream();
        		if (in != null) {
        			try {
        				props.load(in);
        			} catch (Exception e) {
        				try {
        					in.close();
        				} catch (Exception e1) {
        					// ignore.
						}
					}
        		}
        	}
        }
        
//        ResourceBundle
        
        System.out.println(props);
        System.out.println("k1 = " + props.getProperty("k1"));
        System.out.println("k2 = " + props.getProperty("k2"));
        System.out.println("k3 = " + props.getProperty("k3"));
    }

}
