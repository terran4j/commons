package com.terran4j.test.commons.hedis;

import org.mockito.MockitoAnnotations;
import org.springframework.test.context.TestContext;

public class MockitoInitializer extends BaseTestExecutionListener {

	@Override
	public void prepareTestInstance(TestContext testContext) throws Exception {
		// 初始化测试用例类中由Mockito的注解标注的所有模拟对象
		Object instance = testContext.getTestInstance();
		MockitoAnnotations.initMocks(instance);
	}
}
