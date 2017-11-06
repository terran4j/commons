package com.terran4j.commons.util.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.terran4j.commons.util.error.BusinessException;
import com.terran4j.commons.util.error.CommonErrorCode;

public class MD5Util {

	private static final Logger log = LoggerFactory.getLogger(MD5Util.class);

	public static final String ALGORITHM_MD5 = "MD5";

	private static MessageDigest md = null;

	private static final MessageDigest getMessageDigest() throws BusinessException {
		if (md != null) {
			return md;
		}
		synchronized (MD5Util.class) {
			if (md != null) {
				return md;
			}
			try {
				md = MessageDigest.getInstance(ALGORITHM_MD5);
				return md;
			} catch (NoSuchAlgorithmException e) {
				log.error("NoSuchAlgorithmException: " + e.getMessage(), e);
				throw new BusinessException(CommonErrorCode.INTERNAL_ERROR, e).put("algorithm", ALGORITHM_MD5)
						.setMessage("No Such Algorithm: ${algorithm}");
			}
		}
	}

	/**
	 * 给一组数据计算签名。
	 * 
	 * @param data
	 *            数据。
	 * @param secretKey
	 *            密钥。
	 * @return 签名串。
	 * @throws BusinessException
	 */
	public static String signature(Map<String, String> data, String secretKey) throws BusinessException {

		StringBuilder sb = new StringBuilder();

		Object[] keys = data.keySet().toArray();
		Arrays.sort(keys);
		for (Object key : keys) {
			String value = data.get(key);
			if (!StringUtils.isEmpty(value)) {
				sb.append(key + "=" + value + "&");
			}
		}

		String text = "";
		if (sb.length() > 0) {
			text += sb.substring(0, sb.length() - 1);
		}

		text += secretKey;
		return md5(text);
	}

	public static String md5(String text) throws BusinessException {
		try {
			byte[] input = text.getBytes();
			byte[] data = getMessageDigest().digest(input);
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < data.length; i++) {
				sb.append(Integer.toString((data[i] & 0xff) + 0x100, 16).substring(1));
			}
			return sb.toString();
		} catch (BusinessException e) {
			throw e.put("text", text);
		}
	}

}
