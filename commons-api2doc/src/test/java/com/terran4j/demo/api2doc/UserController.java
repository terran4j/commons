package com.terran4j.demo.api2doc;

import com.terran4j.commons.api2doc.Api2DocMocker;
import com.terran4j.commons.api2doc.annotations.Api2Doc;
import com.terran4j.commons.api2doc.annotations.ApiComment;
import com.terran4j.commons.api2doc.annotations.ApiError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api2Doc(id = "users", name = "用户接口", order = 0)
@ApiComment(seeClass = User.class)
@RestController
@RequestMapping(value = "/api2doc/demo")
public class UserController {

    // http://localhost:8080/api2doc/demo/user/1
    @Api2Doc(order = 10)
    @ApiComment("根据用户id，查询此用户的信息")
    @RequestMapping(name = "查询单个用户",
            value = "/user/{id}", method = RequestMethod.GET)
    public User getUser(@PathVariable("id") Long id) {
        return Api2DocMocker.mockObject(User.class);
    }


    /**
     * http://localhost:8080/api2doc/demo/users
     */
    @Api2Doc(order = 20)
    @ApiComment("查询所有用户，按注册时间进行排序。")
    @RequestMapping(name = "查询用户列表",
            value = "/users", method = RequestMethod.GET)
    public List<User> getUsers() {
        return Api2DocMocker.mockList(User.class, 2);
    }

    // http://localhost:8080/api2doc/demo/group/研发组
    @Api2Doc(order = 30)
    @ApiComment("根据指定的组名称，查询该组中的所有用户信息。")
    @RequestMapping(name = "查询用户组",
            value = "/group/{group}", method = RequestMethod.GET)
    public UserGroup getGroup(@PathVariable("group") String group) {
        return Api2DocMocker.mockObject(UserGroup.class);
    }

    // http://localhost:8080/api2doc/demo/user
    @Api2Doc(order = 40)
    @ApiComment("添加一个新的用户。")
    @RequestMapping(name = "新增用户",
            value = "/user", method = RequestMethod.POST)
    public User addUser(String group, String name, UserType type) {
        return Api2DocMocker.mockObject(User.class);
    }

    /**
     * http://localhost:8080/api2doc/demo/user/1
     */
    @Api2Doc(order = 50)
    @ApiComment("根据用户id，删除指定的用户")
    @ApiError(value = "user.not.found", comment = "此用户不存在！")
    @ApiError(value = "admin.cant.delete", comment = "不允许删除管理员用户！")
    @RequestMapping(name = "删除指定用户",
            value = "/user/{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable("id") Long id) {
    }

}
