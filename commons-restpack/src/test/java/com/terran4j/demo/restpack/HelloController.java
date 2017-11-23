package com.terran4j.demo.restpack;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
public class HelloController {

    @RequestMapping("/restpack/hello.html")
    public String hello(
            @RequestParam(value = "name", defaultValue = "world") String name,
            Map<String, Object> model) {
        model.put("name", name);
        return "restpack/hello";
    }

}
