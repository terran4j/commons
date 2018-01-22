package com.terran4j.demo.api2page;

import com.terran4j.commons.api2page.ListResult;
import com.terran4j.commons.api2page.annotation.*;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Api2PageController
@RequestMapping(value = "/api2page")
public class DemoUserController {

    // 列表页面。
    @PageList(value = "/abc/users/list.html", title = "用户列表", tableButtons = {
            @Button(name = "创建", invoke = "preAddUser")
    }, rowButtons = {
            @Button(name = "编辑", invoke = "preEditUser"),
            @Button(name = "删除", invoke = "deleteUser"),
    })
    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public ListResult<User> queryUsers(@ModelAttribute UserQuery query) {
        return null;
    }

    // 新增页面。
    @PageForm(title = "新建用户", buttons = {
            @Button(name = "提交", invoke = "doAddUser")
    })
    @RequestMapping(value = "/addUser", method = RequestMethod.GET)
    public User preAddUser() {
        return null;
    }

    @RequestMapping(value = "/addUser", method = RequestMethod.POST)
    public void doAddUser(@ModelAttribute User user) {
    }

    // 编辑页面。
    @PageForm(title = "编辑用户", buttons = {
            @Button(name = "提交", invoke = "doEditUser")
    })
    @RequestMapping(value = "/editUser", method = RequestMethod.GET)
    public User preEditUser() {
        return null;
    }

    @RequestMapping(value = "/addUser", method = RequestMethod.POST)
    public void doEditUser(@ModelAttribute User user) {
    }

    @RequestMapping(value = "/deleteUser", method = RequestMethod.POST)
    public void deleteUser(@RequestParam("id") long userId) {
    }

}
