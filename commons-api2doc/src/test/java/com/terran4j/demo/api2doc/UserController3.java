package com.terran4j.demo.api2doc;

import com.terran4j.commons.api2doc.annotations.Api2Doc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api2Doc(id = "demo3", name = "演示添加补充文档的用法", order = 30)
@RestController
@RequestMapping(value = "/api2doc/demo3")
public class UserController3 {

    @Api2Doc(order = 10)
    @RequestMapping(name = "接口1", value = "/m1")
    public void m1() {
    }

    @Api2Doc(order = 20)
    @RequestMapping(name = "接口2", value = "/m2")
    public void m2() {
    }

    @RequestMapping(value = "/do_something")
    public void doSomethingRequiredLogon() {
    }
}
