package com.terran4j.demo.api2doc;

import com.terran4j.commons.api2doc.Api2DocMocker;
import com.terran4j.commons.api2doc.annotations.Api2Doc;
import com.terran4j.commons.api2doc.annotations.ApiComment;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api2Doc(id = "users", name = "用户相关接口", order = 0)
@ApiComment(seeClass = User.class)
@RestController
@RequestMapping(value = "/api2doc/demo")
public class UserController {

    // http://localhost:8080/api2doc/demo/user/1
    @Api2Doc(order = 10)
    @RequestMapping(name = "查询单个用户",
            value = "/user/{id}", method = RequestMethod.GET)
    public User getUser(@PathVariable("id") Long id) {
        return Api2DocMocker.mockObject(User.class);
    }


    /**
     * http://localhost:8080/api2doc/demo/users
     */
    @Api2Doc(order = 20)
    @RequestMapping(name = "查询用户列表",
            value = "/users", method = RequestMethod.GET)
    public List<User> getUsers() {
        return Api2DocMocker.mockList(User.class, 2);
    }

    // http://localhost:8080/api2doc/demo/group/研发组
    @Api2Doc(order = 30)
    @RequestMapping(name = "查询用户组",
            value = "/group/{group}", method = RequestMethod.GET)
    public UserGroup getGroup(@PathVariable("group") String group) {
        return Api2DocMocker.mockObject(UserGroup.class);
    }

    // http://localhost:8080/api2doc/demo/user
    @Api2Doc(order = 40)
    @RequestMapping(name = "新增用户",
            value = "/user", method = RequestMethod.POST)
    public User addUser(@RequestParam("id") String id,
                        @RequestParam("name") String name) {
        return Api2DocMocker.mockObject(User.class);
    }

    /**
     * http://localhost:8080/api2doc/demo/user/1
     */
    @Api2Doc(order = 50)
    @RequestMapping(name = "删除指定用户",
            value = "/user/{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable("id") Long id) {
    }

}
