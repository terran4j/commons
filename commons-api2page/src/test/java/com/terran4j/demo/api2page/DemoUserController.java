package com.terran4j.demo.api2page;

import com.terran4j.commons.api2.ApiInfo;
import com.terran4j.commons.api2page.Api2Page;
import com.terran4j.commons.api2page.PageType;
import com.terran4j.commons.restpack.PageResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "/api2page")
public class DemoUserController {

    @Api2Page(id="demo/queryUsers", type = PageType.ListPage)
    @ApiInfo(name = "用户列表")
    @RequestMapping(value = "/queryUsers",
            method = RequestMethod.GET)
    public PageResult<User> queryUsers(@ModelAttribute QueryUserForm form) {
        return null;
    }

    @Api2Page(id="demo/addUser", type = PageType.FormPage)
    @ApiInfo(name = "用户新建")
    @RequestMapping(value = "/addUser",
            method = RequestMethod.POST)
    public void addUser(@ModelAttribute User user) {
    }
}
