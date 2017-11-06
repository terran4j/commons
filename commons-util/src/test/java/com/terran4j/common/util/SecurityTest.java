package com.terran4j.common.util;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.terran4j.commons.util.security.AsymmetricKeys;

@RunWith(SpringJUnit4ClassRunner.class)
public class SecurityTest {
	
	private static final Logger log = LoggerFactory.getLogger(SecurityTest.class);

	@Test
	public void testEncryptAndDecrypt() throws Exception {
		AsymmetricKeys rsa = new AsymmetricKeys();
		String plainText = "Hello, world!"; // 测试字符串
		log.info("plainText: {}", plainText);
		String cipherText = rsa.encrypt(plainText); // 加密
		log.info("cipherText: {}", cipherText);
		String resultText = rsa.decrypt(cipherText); // 解密
		log.info("resultText: {}", resultText);
		Assert.assertEquals(plainText, resultText);
	}
	
	@Test
	public void testCreateByText() throws Exception {
		AsymmetricKeys rsa0 = new AsymmetricKeys();
		String publicKey = rsa0.getPublicKey();
		log.info("publicKey: \n{}", publicKey);
		String privateKey = rsa0.getPrivateKey();
		log.info("privateKey: \n{}", privateKey);
		AsymmetricKeys rsa1 = new AsymmetricKeys(publicKey, privateKey);
		String plainText = "Hello, world!";
		String cipherText = rsa0.encrypt(plainText); // rsa0 加密
		String resultText = rsa1.decrypt(cipherText); // rsa1 解密
		Assert.assertEquals(plainText, resultText);
	}
}
