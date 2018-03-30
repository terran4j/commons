package com.terran4j.demo.api2doc;

import com.terran4j.commons.api2doc.annotations.Api2Doc;
import org.springframework.web.bind.annotation.*;

@Api2Doc(value = "mapping", name = "演示各种Mapping的写法")
@RestController
@RequestMapping(value = "/mapping")
public class ShowMappingController {

    @GetMapping
    @PostMapping
    @PutMapping
    @DeleteMapping
    @PatchMapping
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE,
            name = "删除用户")
    public void deleteUser(@PathVariable Long id) {
    }
}
