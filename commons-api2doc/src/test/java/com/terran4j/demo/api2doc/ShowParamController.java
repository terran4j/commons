package com.terran4j.demo.api2doc;

import com.terran4j.commons.api2doc.annotations.Api2Doc;
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
    public void requestParam(Long id) {
        System.out.println("requestParam, id = " + id);
    }

    @RequestMapping(value = "/path/{id}", name = "@PathVariable")
    public void pathVariable(@PathVariable Long id) {
        System.out.println("pathVariable, id = " + id);
    }

    @RequestMapping(value = "/header", name = "@RequestHeader")
    public void requestHeader(@RequestHeader("key") String key) {
        System.out.println("RequestHeader, key = " + key);
    }

    @RequestMapping(value = "/cookie", method = RequestMethod.GET,
            name = "@CookieValue")
    public void cookieValue(@CookieValue("key") String key) {
        System.out.println("cookieValue, key = " + key);
    }

    @RequestMapping(value = "/part", method = RequestMethod.GET,
            name = "@RequestPart")
    public void requestPart(@RequestPart("file") MultipartFile file) {
        System.out.println("requestPart, file = " + file.getOriginalFilename());
    }

}