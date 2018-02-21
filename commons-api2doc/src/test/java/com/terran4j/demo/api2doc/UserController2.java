package com.terran4j.demo.api2doc;

import com.terran4j.commons.api2doc.annotations.Api2Doc;
import com.terran4j.commons.api2doc.annotations.ApiComment;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api2Doc(id = "demo2", name = "用户接口2")
@ApiComment(seeClass = User.class)
@RestController
@RequestMapping(value = "/api2doc/demo2")
public class UserController2 {

    @ApiComment("根据用户id，查询此用户的信息")
    @RequestMapping(name = "查询单个用户",
            value = "/user/{id}", method = RequestMethod.GET)
    public User getUser(@PathVariable("id") Long id) {
        return null; // TODO:  还未实现。
    }

    @ApiComment("查询所有用户，按注册时间进行排序。")
    @RequestMapping(name = "查询用户列表",
            value = "/users", method = RequestMethod.GET)
    public List<User> getUsers() {
        return null; // TODO:  还未实现。
    }

    @ApiComment("根据指定的组名称，查询该组中的所有用户信息。")
    @RequestMapping(name = "查询用户组",
            value = "/group/{group}", method = RequestMethod.GET)
    public UserGroup getGroup(@PathVariable("group") String group) {
        return null; // TODO:  还未实现。
    }

    @ApiComment("添加一个新的用户。")
    @RequestMapping(name = "新增用户",
            value = "/user", method = RequestMethod.POST)
    public User addUser(String group, String name, UserType type) {
        return null; // TODO:  还未实现。
    }
}
