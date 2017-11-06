package com.terran4j.commons.test;

import org.junit.Test;
import org.springframework.test.context.TestExecutionListeners;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@TestExecutionListeners({ MockitoInitializer.class })
public class MockitoTest {

	/**
	 * mock方法可以基于一个接口生成一个模拟对象， 你可以指定这个模拟对象的行为，比如方法的返回值。
	 */
	@Test
	public void testMock() {
		// 创建mock对象，参数可以是类，也可以是接口
		HelloService mock = mock(HelloService.class);

		// 设置方法的预期返回值
		when(mock.sayHello("terran4j")).thenReturn("Hello terran4j");
		when(mock.getCount()).thenReturn(10);

		// junit测试
		String result = mock.sayHello("terran4j");
		assertEquals("Hello terran4j", result);
		int count = mock.getCount();
		assertEquals(10, count);

		// 验证方法是否调用了sayHello("terran4j")
		verify(mock).sayHello("terran4j");
	}

	/**
	 * spy方法可以模拟一个实际对象的部分行为（比如方法的返回值），但其它未模拟的行为不受影响。
	 */
	@Test
	public void testSpy() {

		// 创建 spy 对象，参数可以是类，也可以是接口
		HelloService hello = new HelloService();
		HelloService spy = spy(hello);

		// 设置 getCount 方法的预期返回值
		doReturn(100).when(spy).getCount();
		// 不设置 sayHello 方法的预期返回值 doReturn("Hello
		// abc").when(spy).sayHello("abc");

		// 未被 spy 模拟的方法不受影响。
		String result = spy.sayHello("terran4j");
		assertEquals("Hello terran4j", result);
		result = hello.sayHello("terran4j");
		assertEquals("Hello terran4j", result);

		// 被 spy 模拟方法返回预设的值。
		hello.sayHello("terran4j");
		hello.sayHello("terran4j");
		assertEquals(3, hello.getCount());
		spy.sayHello("terran4j");
		spy.sayHello("terran4j");
		assertEquals(100, spy.getCount());
	}
}
