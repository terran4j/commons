package com.terran4j.test.website;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HelloController {

    /**
     * http://localhost:8080
     */
    @RequestMapping("/index.html")
    @ResponseBody
    public String hello() {
        return "hello, world";
    }
}
