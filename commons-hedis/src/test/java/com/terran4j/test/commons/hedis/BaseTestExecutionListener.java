package com.terran4j.test.commons.hedis;

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
		SpringBootTest testAnno = testContext.getTestClass().getAnnotation(SpringBootTest.class);
		if (testAnno != null) {
			Class<?>[] springBootClasses = testAnno.classes();
			if (springBootClasses != null && springBootClasses.length > 0) {
				return springBootClasses;
			}
		}
		return new Class<?>[0];
	}

}
