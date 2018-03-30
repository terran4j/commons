package com.terran4j.demo.api2doc;

import com.terran4j.commons.api2doc.annotations.Api2Doc;
import org.springframework.web.bind.annotation.*;

/**
 * TODO: 还未实现，只是计划要实现。
 */
@Api2Doc(value = "param", name = "演示各种参数生成文档")
@RestController
@RequestMapping(value = "/param")
public class ShowParamController {

    @RequestMapping(value = "/path/{id}", name = "@PathVariable")
    public void pathVariable(@PathVariable Long id) {
    }

    @RequestMapping(value = "/header", name = "@RequestHeader")
    public void header(@RequestHeader("key") String key) {
    }

    @RequestMapping(value = "/cookie", method = RequestMethod.GET,
            name = "@CookieValue")
    public void cookie(@CookieValue("key") String key) {
    }

    @RequestMapping(value = "/part", method = RequestMethod.GET,
            name = "@RequestPart")
    public void part(@RequestPart("file") String key) {
    }

}
