package com.terran4j.test.commons.api2doc;

import java.lang.reflect.Method;
import java.util.*;

import com.terran4j.commons.util.value.KeyedList;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.ReflectionUtils;

import com.terran4j.commons.api2doc.domain.ApiResultObject;

@RunWith(SpringJUnit4ClassRunner.class)
public class ApiResultObjectTest {

	private static final Logger log = LoggerFactory.getLogger(ApiResultObjectTest.class);

	public final List<String> getList() {
		return new ArrayList<String>();
	}
	
	public final Set<String> getSet() {
		return new HashSet<String>();
	}
	
	public final String[] getArray() {
		return new String[0];
	}

	public final Map<String, Object> getMap() {
		return new HashMap<String, Object>();
	}

	@Test
	public void testParseResultType() throws Exception {
        log.info("testParseResultType");
        Method method = ReflectionUtils.findMethod(getClass(), "getMap");
        Assert.assertNotNull(method);
        KeyedList<String, ApiResultObject> totalResults = new KeyedList<>();
        ApiResultObject object = ApiResultObject.parseResultType(method, totalResults);

	}

	@Test
	public void testGetReturnArrayClass() throws Exception {
		log.info("testGetReturnArrayClass");
		Method method = ReflectionUtils.findMethod(getClass(), "getList");
		Assert.assertNotNull(method);
		Class<?> clazz = ApiResultObject.getArrayElementClass(method);
		Assert.assertEquals(String.class, clazz);
		
		method = ReflectionUtils.findMethod(getClass(), "getSet");
		Assert.assertNotNull(method);
		clazz = ApiResultObject.getArrayElementClass(method);
		Assert.assertEquals(String.class, clazz);
		
		method = ReflectionUtils.findMethod(getClass(), "getArray");
		Assert.assertNotNull(method);
		clazz = ApiResultObject.getArrayElementClass(method);
		Assert.assertEquals(String.class, clazz);
	}
	
}
