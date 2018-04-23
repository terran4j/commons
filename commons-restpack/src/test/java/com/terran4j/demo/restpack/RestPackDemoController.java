package com.terran4j.demo.restpack;

import com.terran4j.commons.restpack.RestPackController;
import com.terran4j.commons.restpack.log.Log;
import com.terran4j.commons.util.error.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestPackController
@RequestMapping("/demo/restpack")
public class RestPackDemoController {

    private static final Logger log = LoggerFactory.getLogger(RestPackDemoController.class);

    @Log("doEcho")
    private String doEcho(String msg) {
        return msg;
    }

    /**
     * http://localhost:8080/demo/restpack/echo?msg=abc
     * curl "http://localhost:8080/demo/restpack/echo?msg=abc"
     * TODO: 不知为什么，当使用 @GetMapping 时，框架不能自动设置
     * Content-Type 为 application/json;charset=UTF-8 ，
     * 因此这里用 produces 参数手动设置一下。
     */
    @Log("echo")
    @GetMapping(value = "/echo", produces = "application/json;charset=UTF-8")
    public String echo(@RequestParam(value = "msg") String msg) throws BusinessException {
        return doEcho(msg);
    }

    /**
     * http://localhost:8080/demo/restpack/date?time=1522812467402
     */
    @Log
    @RequestMapping(value = "/date", method = RequestMethod.GET)
    public Date toDate(long time) throws BusinessException {
        Date date = new Date(time);
        log.info("echo, date = {}", date);
        return new Date(date.getTime() + 1000);
    }

    /**
     * curl -d "msg=abc" "http://localhost:8080/demo/restpack/void"
     *
     * @param msg
     * @throws BusinessException
     */
    @Log
    @PostMapping(value = "/void")
    public void doVoid(@RequestParam(value = "msg") String msg) throws BusinessException {
        log.info("doVoid, msg = {}", msg);
    }

    /**
     * http://localhost:8080/demo/restpack/hello?name=neo
     *
     * @param name
     * @throws BusinessException
     */
    @Log
    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    public HelloBean hello(String name,
                           @RequestParam(required = false) Date date)
            throws BusinessException {
        log.info("hello, name = {}", name);
        HelloBean bean = new HelloBean();
        bean.setName(name);
        bean.setMessage("Hello, " + name + "!");
        if (date != null) {
            bean.setTime(new Date(date.getTime() + 1000));
        }
        return bean;
    }

}
