package com.terran4j.test.hi.api2doc;

import com.terran4j.commons.api2doc.annotations.Api2Doc;
import com.terran4j.commons.restpack.RestPackController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Api2Doc("test")
@RestPackController
@RequestMapping("/test")
public class Api2DocDemoController {

    @RequestMapping(value = "/multiply", method = RequestMethod.POST)
    public MultiplyObject multiply(long a, long b) {
        return new MultiplyObject(a, b);
    }

    @RequestMapping(value = "/echo", method = RequestMethod.GET)
    public String echo(String msg) {
        return msg;
    }

    @RequestMapping(value = "/put/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public String put(Long id, String name) {
        return id + "-" + name;
    }

    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    @ResponseBody
    public void delete(Long id) {
    }

}
