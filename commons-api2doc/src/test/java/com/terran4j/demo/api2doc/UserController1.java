package com.terran4j.demo.api2doc;

import com.terran4j.commons.api2doc.annotations.Api2Doc;
import com.terran4j.commons.api2doc.annotations.ApiComment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Api2Doc(id = "demo1", name = "用户接口1")
@ApiComment(seeClass = User.class)
@RestController
@RequestMapping(value = "/api2doc/demo1")
public class UserController1 {

    @ApiComment("添加一个新的用户。")
    @RequestMapping(name = "新增用户",
            value = "/user", method = RequestMethod.POST)
    public User addUser(String group, String name,
                        @ApiComment("用户类型") UserType type) {
        return null; // TODO:  还未实现。
    }
}
