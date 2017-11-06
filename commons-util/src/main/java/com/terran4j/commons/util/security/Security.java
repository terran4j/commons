package com.terran4j.commons.util.security;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.terran4j.commons.util.error.BusinessException;

public class Security {

	private static final Logger log = LoggerFactory.getLogger(Security.class);

	/**
	 * 创建一对非对称密钥。
	 * @return 非对称密钥
	 * @throws BusinessException
	 */
	public static final AsymmetricKeys createAsymmetricKeys() throws BusinessException {
		return new AsymmetricKeys();
	}
	
	public static final AsymmetricKeys buildAsymmetricKeys(String publicKey, String privateKey) throws BusinessException {
		if (log.isInfoEnabled()) {
			log.info("build AsymmetricKeys, publicKey = {}, privateKey = {}", publicKey, privateKey);
		}
		return new AsymmetricKeys(publicKey, privateKey);
	}
	
	public static String signature(Map<String, String> data, String secretKey) throws BusinessException {
		return MD5Util.signature(data, secretKey);
	}
}
