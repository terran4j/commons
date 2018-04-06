package com.terran4j.demo.api2doc;

import com.terran4j.commons.api2doc.Api2DocMocker;
import com.terran4j.commons.api2doc.annotations.Api2Doc;
import com.terran4j.commons.api2doc.annotations.ApiComment;
import com.terran4j.commons.restpack.RestPackController;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 */
@Api2Doc(value = "results", name = "演示各种返回类型生成文档", order = 300)
@RestPackController  // 对返回数据进行封装。
@RequestMapping(value = "/api2doc/demo/results")
public class ShowResultController {

    @ApiComment(value = "返回简单的字符串", sample = "getString: abc")
    @GetMapping(value = "/getString", name = "返回简单 String 类型")
    public String getString(String value) {
        String msg = "getString: " + value;
        return msg;
    }

    @ApiComment(value = "返回简单的Date类型", sample = "1522851993490")
    @GetMapping(value = "/getDate", name = "返回简单 Date 类型")
    public Date getDate() {
        return new Date();
    }

    @ApiComment(value = "返回数组类型，其元素为简单 String 类型", sample = "3")
    @GetMapping(value = "/getStringArray", name = "返回简单数组类型")
    public List<String> getStrings() {
        List<String> list = new ArrayList<>();
        list.add("aaa");
        list.add("bbb");
        return list;
    }

    @ApiComment(value = "返回自定义JavaBean类型")
    @GetMapping(value = "/getBean", name = "返回自定义JavaBean类型")
    public User getUser() {
        return Api2DocMocker.mockBean(User.class);
    }

    @ApiComment(value = "返回数组类型，其中的元素为自定义JavaBean类型")
    @GetMapping(value = "/getUsers", name = "返回复杂数组类型")
    public List<User> getUsers() {
        return Api2DocMocker.mockList(User.class, 3);
    }

}