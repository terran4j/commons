package com.terran4j.commons.test;

import com.terran4j.commons.util.Classes;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

public class BaseTestExecutionListener implements TestExecutionListener {

	@Override
	public void beforeTestClass(TestContext testContext) throws Exception {
	}

	@Override
	public void prepareTestInstance(TestContext testContext) throws Exception {
	}

	@Override
	public void beforeTestMethod(TestContext testContext) throws Exception {
	}

	@Override
	public void afterTestMethod(TestContext testContext) throws Exception {
	}

	@Override
	public void afterTestClass(TestContext testContext) throws Exception {
	}
	
	protected final Class<?>[] getSpringBootClasses(TestContext testContext) {
		Class<?> testClass = testContext.getTestClass();
		SpringBootTest testAnno = Classes.getAnnotation(testClass, SpringBootTest.class);
		if (testAnno == null) {
			return new Class<?>[0];
		}
		
		Class<?>[] springBootClasses = testAnno.classes();
		if (springBootClasses != null && springBootClasses.length > 0) {
			return springBootClasses;
		}
		return new Class<?>[0];
	}

}