package com.terran4j.demo.api2doc;

import com.terran4j.commons.api2doc.annotations.Api2Doc;
import com.terran4j.commons.api2doc.annotations.ApiComment;
import com.terran4j.commons.util.Strings;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 *
 */
@Api2Doc(value = "params", name = "演示各种参数生成文档", order = 200)
@RestController
@RequestMapping(value = "/api2doc/demo/params")
public class ShowParamController {

    @ApiComment(value = "返回消息文本", sample = "requestParam, key = 简单")
    @RequestMapping(value = "/param", name = "@RequestParam（默认）")
    public String requestParam(
            @ApiComment(value = "请求参数", sample = "简单") String key) {
        String msg = "requestParam, key = " + key;
        return msg;
    }

    @RequestMapping(value = "/path/{key}", name = "@PathVariable")
    public String pathVariable(
            @ApiComment(value = "路径变量", sample = "我的文档")
            @PathVariable String key) {
        String msg = "pathVariable, key = " + key;
        return msg;
    }

    /**
     * TODO:  header 值不能带中文，或特殊字符。
     */
    @RequestMapping(value = "/header", name = "@RequestHeader")
    public String requestHeader(
            @ApiComment(value = "Header 值 h1", sample = "abc")
            @RequestHeader("h1") String h1,
            @ApiComment(value = "Header 值 h2", sample = "123")
            @RequestHeader("h2") String h2) {
        String msg = "RequestHeader, h1 = " + h1 + ", h2 = " + h2;
        return msg;
    }

    /**
     * TODO: Cookie 中不能有空格，否则传到服务端是 + 号。
     *
     * @param c1
     * @param c2
     * @return
     */
    @RequestMapping(value = "/cookie", method = RequestMethod.GET,
            name = "@CookieValue")
    public String cookieValue(
            @ApiComment(value = "Cookie 值 c1", sample = "我的 Cookie 1")
            @CookieValue("c1") String c1,
            @ApiComment(value = "Cookie 值 c2", sample = "我的 Cookie 2")
            @CookieValue("c2") String c2) {
        String msg = "cookieValue, c1 = " + c1 + ", c2 = " + c2;
        return msg;
    }

    @RequestMapping(value = "/part", method = RequestMethod.POST,
            name = "@RequestPart")
    public FileInfo[] requestPart(
            @ApiComment(value = "文件名称", sample = "my-file")
                    String name,
            @ApiComment(value = "上传文件1", sample = "我的文件1.txt")
            @RequestPart("file1") MultipartFile file1,
            @ApiComment(value = "上传文件2", sample = "我的文件2.txt")
            @RequestPart("file2") MultipartFile file2) throws IOException {
        return new FileInfo[]{
                FileInfo.parse(file1, name + "-1"),
                FileInfo.parse(file2, name + "-2")
        };
    }

}