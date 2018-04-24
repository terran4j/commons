package com.terran4j.demo.restpack;

import com.terran4j.commons.restpack.HttpResult;
import com.terran4j.commons.restpack.HttpResultConverter;
import org.springframework.stereotype.Service;

@Service
public class DemoHttpResultConverter implements HttpResultConverter {

    @Override
    public Object convert(HttpResult httpResult) {
        // 这里可以将 HttpResult 对象转成你需要的格式
        // RestPack 框架会将本方法的返回对象转成 JSON 串返回给请求方。
        return httpResult;
    }
}