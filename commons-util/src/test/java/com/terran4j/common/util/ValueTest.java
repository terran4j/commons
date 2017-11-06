package com.terran4j.common.util;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.StringUtils;

import com.terran4j.commons.util.value.MapValueSource;
import com.terran4j.commons.util.value.ValueWrapper;

@RunWith(SpringJUnit4ClassRunner.class)
public class ValueTest {

	private static final Logger log = LoggerFactory.getLogger(ValueTest.class);
	
	private Map<String, String> getConfigs() {
		Map<String, String> config = new HashMap<>();
		config.put("count", "123");
		return config;
	}

	public void demo() {
		// 将配置文件数据读到一个 map 对象中。
		Map<String, String> configs = getConfigs();

		// 取键为"count"的值，并按int值处理，如果没有或格式不是数字，则取默认值为 0.
		int valueAsInt = 0;
		String key = "count";
		String value = configs.get(key);
		if (!StringUtils.isEmpty(value)) {
			try {
				valueAsInt = Integer.parseInt(value.trim());
			} catch (NumberFormatException e) {
				log.error("key[{}] expect int format, but the value is: {}", key, value);
			}
		} else {
			log.error("key[{}]'s value is null", key);
		}
		Assert.assertEquals(123, valueAsInt);
	}
	
	@Test
	public void testGetValue() {
		// 将配置文件数据读到一个 map 对象中。
		Map<String, String> configs = getConfigs();
		
		// 将 map 包装成 ValueSource 形式，放到 ValueWrapper 中，再用 ValueWrapper 工具类来取值。
		ValueWrapper values = new ValueWrapper(new MapValueSource<String, String>(configs));
		Assert.assertEquals(123, values.get("count", 0));
	}
}
