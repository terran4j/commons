package com.terran4j.demo.commons.api2doc;

import java.util.List;

import com.terran4j.commons.api2doc.Api2DocMocker;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.terran4j.commons.api2doc.annotations.Api2Doc;
import com.terran4j.commons.api2doc.annotations.ApiComment;

@Api2Doc(id = "users", name = "用户相关接口", order = 0)
@ApiComment(seeClass = DemoUser.class)
@RestController
@RequestMapping(value = "/api2doc/demo")
public class UserController {

    /**
     * http://localhost:8080/api2doc/demo/user/1
     */
    @Api2Doc(order = 10)
    @RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
    public DemoUser getUser(@PathVariable("id") Long id) {
        return Api2DocMocker.mockObject(DemoUser.class);
    }

    /**
     * http://localhost:8080/api2doc/demo/users
     */
    @Api2Doc(order = 2)
    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public List<DemoUser> getUserList() {
        return Api2DocMocker.mockList(DemoUser.class, 2);
    }

    /**
     * http://localhost:8080/api2doc/demo/user/1
     */
    @Api2Doc(order = 3)
    @RequestMapping(value = "/user/{id}", method = RequestMethod.POST)
    public DemoUser insert(
            @PathVariable("id") Long id,
            @RequestParam("name") String name) {
        return Api2DocMocker.mockObject(DemoUser.class);
    }

    /**
     * http://localhost:8080/api2doc/demo/user/1
     */
    @Api2Doc(order = 4)
    @RequestMapping(value = "/user/{id}", method = RequestMethod.DELETE)
    public String delete(@PathVariable("id") Long id) {
        return "OK";
    }
}
