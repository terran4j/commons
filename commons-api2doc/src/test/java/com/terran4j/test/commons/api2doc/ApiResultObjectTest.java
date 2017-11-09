package com.terran4j.test.commons.api2doc;

import java.lang.reflect.Method;
import java.util.*;

import com.terran4j.commons.api2doc.domain.ApiDataType;
import com.terran4j.commons.util.value.KeyedList;
import com.terran4j.demo.commons.api2doc.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.ReflectionUtils;

import com.terran4j.commons.api2doc.domain.ApiResultObject;

import javax.validation.constraints.AssertTrue;

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

	public final List<User> getUsers() {
	    return new ArrayList<>();
    }

    @Test
    public void testParseResultType() throws Exception {
        log.info("testParseResultType");
        Method method = ReflectionUtils.findMethod(getClass(), "getUsers");
        Assert.assertNotNull(method);
        KeyedList<String, ApiResultObject> totalResults = new KeyedList<>();
        ApiResultObject object = ApiResultObject.parseResultType(method, totalResults);
        Assert.assertNotNull(object);
        Assert.assertEquals(1, totalResults.size());
        Assert.assertTrue(object == totalResults.get(0));
        Assert.assertTrue(ApiDataType.ARRAY == object.getDataType());
        Assert.assertTrue(User.class == object.getSourceType());
    }

	@Test
	public void testGetArrayElementClass() throws Exception {
		log.info("testGetArrayElementClass");
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
