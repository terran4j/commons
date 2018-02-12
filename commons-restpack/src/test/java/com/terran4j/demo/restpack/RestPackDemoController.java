package com.terran4j.demo.restpack;

import com.terran4j.commons.restpack.RestPackController;
import com.terran4j.commons.util.error.BusinessException;
import com.terran4j.commons.util.error.ErrorCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

@RestPackController
@RequestMapping("/demo/restpack")
public class RestPackDemoController {

    private static final Logger log = LoggerFactory.getLogger(RestPackDemoController.class);

    /**
     * http://localhost:8080/demo/restpack/echo?msg=abc
     *
     * @param msg
     * @return
     * @throws BusinessException
     */
    @RequestMapping(value = "/echo", method = RequestMethod.GET)
    public String echo(@RequestParam(value = "msg") String msg) throws BusinessException {
        log.info("echo, msg = {}", msg);
        return msg;
    }

    @RequestMapping(value = "/date", method = RequestMethod.GET)
    public Date toDate(Date time) throws BusinessException {
        log.info("echo, time = {}", time);
        return new Date(time.getTime() + 1000);
    }

    /**
     * http://localhost:8080/demo/restpack/void?msg=abc
     *
     * @param msg
     * @throws BusinessException
     */
    @RequestMapping(value = "/void", method = RequestMethod.GET)
    public void doVoid(@RequestParam(value = "msg") String msg) throws BusinessException {
        log.info("doVoid, msg = {}", msg);
    }

    /**
     * http://localhost:8080/demo/restpack/error?msg=abc
     *
     * @param msg
     * @throws BusinessException
     */
    @RequestMapping(value = "/error", method = RequestMethod.GET)
    public void toError(@RequestParam(value = "msg") String msg) throws BusinessException {
        log.info("error, msg = {}", msg);
        throw new BusinessException(ErrorCodes.INVALID_PARAM)
                .setMessage("无效的输入: ${msg}").put("msg", msg);
    }

    /**
     * http://localhost:8080/demo/restpack/error2?msg=abc
     *
     * @param msg
     * @throws Exception
     */
    @RequestMapping(value = "/error2", method = RequestMethod.GET)
    public void toError2(@RequestParam(value = "msg") String msg) throws Exception {
        log.info("error, msg = {}", msg);
        throw new RuntimeException("error msg.");
    }

    /**
     * http://localhost:8080/demo/restpack/hello?name=neo
     *
     * @param name
     * @throws BusinessException
     */
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
