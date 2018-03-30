package com.terran4j.demo.api2doc;

import com.terran4j.commons.api2doc.annotations.Api2Doc;
import com.terran4j.commons.api2doc.annotations.ApiComment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

/**
 * TODO: 还未实现，只是计划要实现。
 */
@Api2Doc(value = "mapping", name = "演示各种Mapping生成文档")
@RestController
@RequestMapping(value = "/mapping")
public class ShowMappingController {

    private static final Logger log = LoggerFactory.getLogger(ShowMappingController.class);

    @GetMapping(value = "/doGet/{id}", name = "演示 @GetMapping 方法")
    public void doGet(
            @ApiComment("用户id") @PathVariable("id") Long id) {
        if (log.isInfoEnabled()) {
            log.info("doGet, id = {}", id);
        }
    }

    @PostMapping(value = "/doPost/{id}", name = "演示 @PostMapping 方法")
    public void doPost(
            @ApiComment("用户id") @PathVariable("id") Long id,
            @ApiComment("用户描述") String desc) {
        if (log.isInfoEnabled()) {
            log.info("doPost, id = {}, desc = {}", id, desc);
        }
    }

    @PutMapping(value = "/doPut", name = "演示 @PutMapping 方法")
    public void doPut(
            @ApiComment("用户id") @PathVariable("id") Long id,
            @ApiComment("用户描述") String desc) {
        if (log.isInfoEnabled()) {
            log.info("doPut, id = {}, desc = {}", id, desc);
        }
    }

    @DeleteMapping(value = "/doDelete/{id}", name = "演示 @DeleteMapping 方法")
    public void doDelete(
            @ApiComment("用户id") @PathVariable("id") Long id) {
        if (log.isInfoEnabled()) {
            log.info("doDelete, id = {}", id);
        }
    }

    @PatchMapping(value = "/doPatch", name = "演示 @PatchMapping 方法")
    public void doPatch(
            @ApiComment("用户id") @PathVariable("id") Long id,
            @ApiComment("用户描述") String desc) {
        if (log.isInfoEnabled()) {
            log.info("doPatch, id = {}, desc = {}", id, desc);
        }
    }
}
