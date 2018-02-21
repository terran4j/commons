package com.terran4j.commons.test;

import com.terran4j.commons.hi.HttpClientListener;
import com.terran4j.commons.hi.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SigListener implements HttpClientListener {

	private static final Logger log = LoggerFactory.getLogger(SigListener.class);
	
	private final String appSecret;
	
	private final Set<String> pathPrefixs = new HashSet<>();
	
	public SigListener(String appSecret) {
		super();
		this.appSecret = appSecret;
	}
	
	public SigListener addPathPrefix(String pathPrefix) {
		if (StringUtils.isEmpty(pathPrefix)) {
			return this;
		}
		
		pathPrefixs.add(pathPrefix);
		return this;
	}

	@Override
	public void beforeExecute(HttpRequest request) {
		String url = request.getUrl();
		
		boolean matched = false;
		if (pathPrefixs.size() > 0) {
			for (String pathPrefix : pathPrefixs) {
				if (url.startsWith(pathPrefix)) {
					matched = true;
					break;
				}
			}
		} else {
			matched = true;
		}
		if (!matched) {
			return;
		}
		
		Map<String, String> params = request.getParams();
		if (params == null) {
			return;
		}
		
		String sig = signature(params, appSecret);
		params.put("sig", sig);
		if (log.isInfoEnabled()) {
			log.info("do beforeExecute, sig = {}", sig);
		}
	}
	
	@Override
	public String afterExecute(HttpRequest request, String reponse) {
		return reponse;
	}
	
	/**
     * 计算签名
     *
     * @param requestParams
     * @return
     */
    public static String signature(Map<String, String> requestParams, String appSecret) {

        StringBuilder buffer = new StringBuilder();

        Object[] keys = requestParams.keySet().toArray();
        Arrays.sort(keys);
        for (Object key : keys) {
            String value = requestParams.get(key);
            if (!"sig".equals(key) && !StringUtils.isEmpty(value)) {
                buffer.append(key + "=" + value + "&");
            }
        }

        String sign = "";
        if (buffer.length() > 0) {
            sign += buffer.substring(0, buffer.length() - 1);
        }

        sign += appSecret;
        return md5(sign);
    }

    private static String md5(String input) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
        }

        md.update(input.getBytes());
        byte byteData[] = md.digest();
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
            buffer.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }
        return buffer.toString();
    }

}
