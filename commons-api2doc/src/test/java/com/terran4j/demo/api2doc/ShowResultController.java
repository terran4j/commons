package com.terran4j.demo.api2doc;

import com.terran4j.commons.api2doc.annotations.Api2Doc;
import com.terran4j.commons.api2doc.annotations.ApiComment;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 */
@Api2Doc(value = "results", name = "演示各种返回类型生成文档", order = 300)
@RestController
@RequestMapping(value = "/api2doc/demo/results")
public class ShowResultController {

    @ApiComment(value = "返回简单的字符串", sample = "getString: abc")
    @RequestMapping(value = "/getString", name = "返回简单 String 类型")
    public String getString(String value) {
        String msg = "getString: " + value;
        return msg;
    }

    @ApiComment(value = "返回简单的Date类型", sample = "1522851993490")
    @RequestMapping(value = "/getDate", name = "返回简单 Date 类型")
    public Date getDate() {
        return new Date();
    }

    @ApiComment(value = "返回数组类型，其元素为简单 String 类型", sample = "3")
    @RequestMapping(value = "/getStringArray", name = "返回简单数组类型")
    public List<String> getStrings() {
        List<String> list = new ArrayList<>();
        list.add("aaa");
        list.add("bbb");
        return list;
    }

    @ApiComment(value = "返回自定义JavaBean类型")
    @RequestMapping(value = "/getBean", name = "返回自定义JavaBean类型")
    public User getBean() {
        return new User();
    }

}