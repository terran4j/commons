package com.terran4j.commons.hedis.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.terran4j.commons.util.Strings;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.terran4j.commons.util.error.BusinessException;
import com.terran4j.commons.util.error.CommonErrorCode;

public class RedisTemplateCacheService implements CacheService {

	private static Logger log = LoggerFactory.getLogger(RedisTemplateCacheService.class);
	
	private static final Gson g = new Gson();

	private RedisTemplate<String, String> redisTemplate;
	
	public RedisTemplateCacheService(RedisTemplate<String, String> redisTemplate) {
		super();
		this.redisTemplate = redisTemplate;
		if (log.isInfoEnabled()) {
			log.info("created RedisTemplateCacheService.");
		}
	}

	@Override
	public boolean existed(String key) {
		if (StringUtils.isBlank(key)) {
			throw new NullPointerException("key is null.");
		}
		return redisTemplate.opsForValue().getOperations().hasKey(key);
	}

	@Override
	public void remove(String key) {
		if (StringUtils.isBlank(key)) {
			throw new NullPointerException("key is null.");
		}
		redisTemplate.opsForValue().getOperations().delete(key);
	}

	@Override
	public void setObject(String key, Object value, Long keepTime) {
		if (StringUtils.isBlank(key)) {
			throw new NullPointerException("key is null.");
		}
		if (value == null) {
			throw new NullPointerException("value is null.");
		}

		String valueText = g.toJson(value);
		// 如果keepTime值为null或keepTime值小于等于0，那么都按永久生效处理
		if (keepTime == null || keepTime <= 0L) {
			redisTemplate.opsForValue().set(key, valueText);
		} else {
			redisTemplate.opsForValue().set(key, valueText, keepTime, TimeUnit.MILLISECONDS);
		}
	}

	@Override
	public <T> T getObject(String key, Class<T> clazz) throws BusinessException {
		if (StringUtils.isBlank(key)) {
			throw new NullPointerException("key is null");
		}
		if (clazz == null) {
			throw new NullPointerException("clazz is null");
		}

		T t = null;
		String value = redisTemplate.opsForValue().get(key);
		if (!StringUtils.isBlank(value)) {
			try {
				t = g.fromJson(value, clazz);
			} catch (JsonSyntaxException e) {
				throw new BusinessException(CommonErrorCode.JSON_ERROR, e) //
						.put("methodName", "getObject") //
						.put("key", key).put("clazz", clazz).put("value", value) //
						.setMessage("${methodName}方法, key=${key}, value=${value}, 解析json串失败: " + e.getMessage());
			}
		}
		return t;
	}

	@Override
	public void setHashEntry(String key, String entryKey, Object entryValue, Long keepTime) {
		if (StringUtils.isBlank(key)) {
			throw new NullPointerException("key is blank");
		}
		if (StringUtils.isBlank(entryKey)) {
			throw new NullPointerException("entryKey is blank");
		}
		if (entryValue == null) {
			throw new NullPointerException("entryValue is null");
		}

		String valueText = g.toJson(entryValue);
		redisTemplate.opsForHash().put(key, entryKey, valueText);
		
		// 如果keepTime值为null或keepTime值小于等于0，那么都按永久生效处理
		if (keepTime != null && keepTime > 0) {
			redisTemplate.expire(key, keepTime, TimeUnit.MILLISECONDS);
		}
	}

	@Override
	public <T> T getHashEntry(String key, String entryKey, Class<T> clazz) throws BusinessException {
		if (StringUtils.isBlank(key)) {
			throw new NullPointerException("key is blank");
		}
		if (StringUtils.isBlank(entryKey)) {
			throw new NullPointerException("entryKey is blank");
		}
		if (clazz == null) {
			throw new NullPointerException("clazz is null");
		}

		Gson g = new Gson();
		T t = null;
		String value = (String) redisTemplate.opsForHash().get(key, entryKey);
		if (!StringUtils.isBlank(value)) {
			try {
				t = g.fromJson(value, clazz);
			} catch (JsonSyntaxException e) {
				throw new BusinessException(CommonErrorCode.JSON_ERROR, e).put("methodName", "getHashEntry")
						.put("key", key).put("clazz", clazz).put("value", value)
						.setMessage("${methodName}方法, key=${key}, value=${value}, 解析json串失败: " + e.getMessage());
			}
		}
		return t;
	}

	@Override
	public <T> void setHashMap(String key, Map<String, T> map, Class<T> clazz) {
		if (StringUtils.isBlank(key)) {
			throw new NullPointerException("key is blank");
		}
		if (map == null) {
			throw new NullPointerException("map is null");
		}
		if (clazz == null) {
			throw new NullPointerException("clazz is null");
		}

		Map<String, String> temp = new HashMap<String, String>();
		for (Map.Entry<String, T> entry : map.entrySet()) {
			temp.put(entry.getKey(), g.toJson(entry.getValue(), clazz));
		}
		redisTemplate.opsForHash().putAll(key, temp);
	}

	@Override
	public <T> Map<String, T> getHashMap(String key, Class<T> clazz) throws BusinessException {
		if (StringUtils.isBlank(key)) {
			throw new NullPointerException("key is blank");
		}
		if (clazz == null) {
			throw new NullPointerException("clazz is null");
		}

		Map<String, T> result = new HashMap<String, T>();
		Map<Object, Object> temp = redisTemplate.opsForHash().entries(key);
		if (temp == null || temp.size() == 0) {
			return result;
		}
		
		for (Map.Entry<Object, Object> entry : temp.entrySet()) {
			try {
				result.put((String) entry.getKey(), g.fromJson((String) entry.getValue(), clazz));
			} catch (JsonSyntaxException e) {
				throw new BusinessException(CommonErrorCode.JSON_ERROR, e).put("methodName", "getHashMap")
						.put("key", key).put("clazz", clazz).put("value", entry.getValue())
						.setMessage("${methodName}方法, key=${key}, value=${value}, 解析json串失败: " + e.getMessage());
			}
		}
		
		return result;
	}

	@Override
	public boolean setObjectIfAbsent(String key, Object value, Long expiredMillisecond) {
		if (StringUtils.isBlank(key)) {
			throw new NullPointerException("key is blank");
		}
		if (value == null) {
			throw new NullPointerException("value is null");
		}
		
		String valueText = g.toJson(value);
		boolean success = redisTemplate.opsForValue().setIfAbsent(key, valueText);
		if (success && expiredMillisecond != null && expiredMillisecond > 0) {
			redisTemplate.expire(key, expiredMillisecond, TimeUnit.MILLISECONDS);
		}
		
		return success;
	}

	@Override
	public <T> boolean sendMessage(String channel, T message) throws BusinessException {
		if (Strings.isNull(channel)) {
			return false;
		}
		try {
			redisTemplate.convertAndSend(channel, message);
			log.info("发送消息成功，channel：{}，message：{}", channel, message);
			return true;
		} catch (Exception e) {
			log.info("发送消息失败，channel：{}，message：{}", channel, message);
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public Object deserialize(byte[] bytes) {
		return redisTemplate.getValueSerializer().deserialize(bytes);
	}
}
