package com.terran4j.test.hi.exe;

import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RequestMapping("/exe")
@RestController
public class ExeController {

    @RequestMapping(value = "/plus", method = RequestMethod.POST)
    @ResponseBody
    public PlusObject plus(long a, long b) {
        return new PlusObject(a, b);
    }

    @RequestMapping(value = "/echo", method = RequestMethod.GET)
    @ResponseBody
    public String echo(HttpServletRequest request) {
        return request.getQueryString();
    }

}
