package com.terran4j.demo.api2doc;

import com.terran4j.commons.api2doc.annotations.Api2Doc;
import com.terran4j.commons.api2doc.annotations.ApiComment;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * TODO: 还未实现，只是计划要实现。
 */
@Api2Doc(value = "params", name = "演示各种参数生成文档")
@RestController
@RequestMapping(value = "/params")
public class ShowParamController {

    @RequestMapping(value = "/param", name = "@RequestParam（默认）")
    public void requestParam(
            @ApiComment(value = "请求参数", sample = "简单") String key) {
        System.out.println("requestParam, key = " + key);
    }

    @RequestMapping(value = "/path/{key}", name = "@PathVariable")
    public void pathVariable(
            @ApiComment(value = "路径变量", sample = "我的文档")
            @PathVariable String key) {
        System.out.println("pathVariable, key = " + key);
    }

    @RequestMapping(value = "/header", name = "@RequestHeader")
    public void requestHeader(
            @ApiComment(value = "Header 值", sample = "我的 Header")
            @RequestHeader("key") String key) {
        System.out.println("RequestHeader, key = " + key);
    }

    @RequestMapping(value = "/cookie", method = RequestMethod.GET,
            name = "@CookieValue")
    public void cookieValue(
            @ApiComment(value = "Cookie 值", sample = "我的 Cookie")
            @CookieValue("key") String key) {
        System.out.println("cookieValue, key = " + key);
    }

    @RequestMapping(value = "/part", method = RequestMethod.GET,
            name = "@RequestPart")
    public void requestPart(
            @ApiComment(value = "上传的文件", sample = "我的文件.txt")
            @RequestPart("file") MultipartFile file) {
        System.out.println("requestPart, file = " + file.getOriginalFilename());
    }

}