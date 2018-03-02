package com.terran4j.demo.hi;

import com.terran4j.commons.hi.HttpClient;
import com.terran4j.commons.hi.HttpException;
import com.terran4j.commons.hi.Response;
import com.terran4j.commons.hi.Session;
import org.junit.Assert;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class HttpClientApp {

    public static void main(String[] args) throws HttpException {
        ApplicationContext context = SpringApplication.run(HttpClientApp.class, args);

        // 创建一个 httpClient 对象，它会加载 http.config.json 文件中定义的 HTTP API 信息。
        HttpClient httpClient = HttpClient.create(
                HttpClientApp.class, "demo.json", context);

        // 创建一个 session，它会在客户端维护一些会话信息。
        Session session = httpClient.create();

        // 调用 plus 接口，输入参数 input = 3.
        Response response = session.createRequest("plus").param("input", "3").exe();

        // 从返回结果中，取 result 字段的值。
        int total = response.getJson("result").getAsInt();
        Assert.assertEquals(3, total);

        // 再调用一次，发现返回结果的确在累加。
        response = session.createRequest("plus").param("input", "5").exe();
        total = response.getJson("result").getAsInt();
        Assert.assertEquals(8, total);
    }

}
