package com.terran4j.test.commons.api2doc;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.ReflectionUtils;

import com.terran4j.commons.api2doc.codewriter.JavaBeanCodeWriter;
import com.terran4j.commons.api2doc.codewriter.MemoryCodeOutput;
import com.terran4j.commons.api2doc.domain.ApiResultObject;
import com.terran4j.commons.util.value.KeyedList;

@SpringBootTest(classes = { Application.class }, webEnvironment = WebEnvironment.NONE)
@RunWith(SpringJUnit4ClassRunner.class)
public class JavaBeanCodeWriterTest {

	private static final Logger log = LoggerFactory.getLogger(JavaBeanCodeWriterTest.class);

	public class User {

		private String name;
		
		private Date registTime;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Date getRegistTime() {
			return registTime;
		}

		public void setRegistTime(Date registTime) {
			this.registTime = registTime;
		}

	}
	
	public final User getUser() {
		return new User();
	}
	
	@Autowired
	private JavaBeanCodeWriter javaBeanCodeWriter;
	
	@Test
	public void testGetModel() throws Exception {
		log.info("testGetModel");
		Method method = ReflectionUtils.findMethod(getClass(), "getUser");
		KeyedList<String, ApiResultObject> totalResults = new KeyedList<>();
		ApiResultObject user = ApiResultObject.parseResultType(method, totalResults);
		
		Map<String, Object> model = javaBeanCodeWriter.getModel(
		        user, "User", null);
		@SuppressWarnings("unchecked")
		Set<String> imports = (Set<String>)model.get("imports");
		log.info("imports: {}", imports);
		Assert.assertFalse(imports.contains(Date.class.getName()));
		
		MemoryCodeOutput out = new MemoryCodeOutput();
		javaBeanCodeWriter.writeCode(user, "User", out, null);
		String code = out.getCode("User.java");
		log.info("User.java:\n{}", code);
	}
	
}
