package com.terran4j.commons.http;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.terran4j.commons.httpinvoker.HttpException;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { HttpClientApp.class }, webEnvironment = WebEnvironment.DEFINED_PORT)
public class HttpClientTest {
	
	@Autowired
	protected ApplicationContext context;
	
	@Test
	public void testHttpClient() throws HttpException {
		
//		// 创建一个 httpClient 对象，它会加载 http.config.json 文件中定义的 HTTP API 信息。
//		HttpClient httpClient = HttpClient.create(context);
//		
//		// 创建一个 session，它会在客户端维护一些会话信息。
//		Session session = httpClient.create();
//		
//		// 调用 plus 接口，输入参数 input = 3.
//		Response response = session.action("plus").param("input", "3").exe();
//		
//		// 从返回结果中，取 result 字段的值。
//		int total = response.getJson("result").getAsInt();
//		Assert.assertEquals(3, total);
//		
//		// 再调用一次，发现返回结果的确在累加。
//		response = session.action("plus").param("input", "5").exe();
//		total = response.getJson("result").getAsInt();
//		Assert.assertEquals(8, total);
	}
	

}
