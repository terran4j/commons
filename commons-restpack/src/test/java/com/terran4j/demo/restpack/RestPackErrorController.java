package com.terran4j.demo.restpack;

import com.terran4j.commons.restpack.RestPackController;
import com.terran4j.commons.util.error.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@RestPackController
@RequestMapping("/demo/restpack/error")
public class RestPackErrorController {

    private static final Logger log = LoggerFactory.getLogger(RestPackErrorController.class);

    /**
     * 方法内部抛出的 BusinessException 异常。
     * http://localhost:8080/demo/restpack/error/be?msg=abc
     */
    @RequestMapping(value = "/be", method = RequestMethod.GET)
    public void toError(@RequestParam(value = "msg") String msg) throws BusinessException {
        log.info("error, msg = {}", msg);
        throw new BusinessException("invalid.msg")
                .setMessage("消息格式无效: ${msg}")
                .put("msg", msg);
    }

    /**
     * 方法内部抛出的 RuntimeException 异常。
     * http://localhost:8080/demo/restpack/error/re?msg=abc
     */
    @RequestMapping(value = "/re", method = RequestMethod.GET)
    public void toError2(@RequestParam(value = "msg") String msg) throws Exception {
        log.info("error, msg = {}", msg);
        throw new RuntimeException("error msg.");
    }

    /**
     * 参数类型是基本类型（这意味着不允许为null），但这个参数却没传。
     * http://localhost:8080/demo/restpack/error/be/pin
     */
    @RequestMapping(value = "/be/pin", method = RequestMethod.GET)
    public void toErrorByPrimitiveIsNull(long id) {
        log.info("error, id = {}", id);
    }



}
