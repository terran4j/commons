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
@Api2Doc(value = "params", name = "演示各种参数生成文档")
@RestController
@RequestMapping(value = "/params")
public class ShowParamController {

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

    @RequestMapping(value = "/part", method = RequestMethod.PUT,
            name = "@RequestPart")
    public FileInfo requestPart(
            @ApiComment(value = "上传的文件", sample = "我的文件.txt")
            @RequestPart("file") MultipartFile file) throws IOException {
        String name = file.getOriginalFilename();
        String msg = "requestPart, file = " + name;
        String content = Strings.getString(file.getInputStream());
        return new FileInfo(name, content, msg);
    }

    private static final class FileInfo {

        private String name;

        private String content;

        private String msg;

        public FileInfo() {
        }

        public FileInfo(String name, String content, String msg) {
            this.name = name;
            this.content = content;
            this.msg = msg;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }
    }

}