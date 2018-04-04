package com.terran4j.demo.api2doc;

import com.terran4j.commons.api2doc.annotations.Api2Doc;
import com.terran4j.commons.api2doc.annotations.ApiComment;
import com.terran4j.commons.restpack.RestPackController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

/**
 *
 */
@Api2Doc(value = "mapping", name = "演示各种Mapping生成文档", order = 100)
@RestPackController
@RequestMapping(value = "/api2doc/demo/mapping")
public class ShowMappingController {

    private static final Logger log = LoggerFactory.getLogger(ShowMappingController.class);

    @ApiComment(value = "返回 doRequest 的消息文本", sample = "doRequest, id = 123")
    @RequestMapping(value = "/doRequest/{id}", name = "演示 @RequestMapping 方法")
    public String doRequest(
            @ApiComment("用户id") @PathVariable("id") Long id) {
        if (log.isInfoEnabled()) {
            log.info("doRequest, id = {}", id);
        }
        return "doRequest, id = " + id;
    }

    @GetMapping(value = "/doGet/{id}", name = "演示 @GetMapping 方法")
    public String doGet(
            @ApiComment("用户id") @PathVariable("id") Long id) {
        if (log.isInfoEnabled()) {
            log.info("doGet, id = {}", id);
        }
        return "doGet, id = " + id;
    }

    @PostMapping(value = "/doPost/{id}", name = "演示 @PostMapping 方法")
    public String doPost(
            @ApiComment("用户id") @PathVariable("id") Long id,
            @ApiComment("用户描述") String desc) {
        if (log.isInfoEnabled()) {
            log.info("doPost, id = {}, desc = {}", id, desc);
        }
        return "doPost, id = " + id + ", desc = " + desc;
    }

    @PutMapping(value = "/doPut/{id}", name = "演示 @PutMapping 方法")
    public String doPut(
            @ApiComment("用户id") @PathVariable("id") Long id,
            @ApiComment("用户描述") String desc) {
        if (log.isInfoEnabled()) {
            log.info("doPut, id = {}, desc = {}", id, desc);
        }
        return "doPut, id = " + id + ", desc = " + desc;
    }

    @DeleteMapping(value = "/doDelete/{id}", name = "演示 @DeleteMapping 方法")
    public String doDelete(
            @ApiComment("用户id") @PathVariable("id") Long id) {
        if (log.isInfoEnabled()) {
            log.info("doDelete, id = {}", id);
        }
        return "doDelete, id = " + id;
    }

    @PatchMapping(value = "/doPatch/{id}", name = "演示 @PatchMapping 方法")
    public String doPatch(
            @ApiComment("用户id") @PathVariable("id") Long id,
            @ApiComment("用户描述") String desc) {
        if (log.isInfoEnabled()) {
            log.info("doPatch, id = {}, desc = {}", id, desc);
        }
        return "doDelete, id = " + id + ", desc = " + desc;
    }
}
