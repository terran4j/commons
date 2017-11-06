package com.terran4j.common.util;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.terran4j.commons.util.Strings;

@RunWith(SpringJUnit4ClassRunner.class)
public class StringsTest {
	
	private static final Logger log = LoggerFactory.getLogger(StringsTest.class);

	@Test
	public void testHex2String() throws Exception {
		String sourceText = "Hello, world!";
		byte[] sourceData = sourceText.getBytes();
		log.info("sourceData = {}", sourceData);
		String hexText = Strings.toHexString(sourceData);
		log.info("hexText = {}", hexText);
		byte[] resolvedData = Strings.fromHexString(hexText);
		log.info("resolvedData = {}", resolvedData);
		String resultText = new String(resolvedData);
		log.info("resultText = {}", resultText);
		Assert.assertEquals(sourceText, resultText);
	}
}
