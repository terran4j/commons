package com.terran4j.commons.hedis.cache;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.terran4j.commons.util.error.BusinessException;
import com.terran4j.commons.util.error.CommonErrorCode;

import redis.clients.jedis.Jedis;

/**
 * 操作 jedis 不能支持并发访问，如果用 synchronized 同步效率会非常低下。<br>
 * 因此弃用，将使用 {@link RedisTemplateCacheService}，这个里面有连接池的管理，不用做线程同步。
 * @author wei.jiang
 *
 */
@Deprecated
public class JedisCacheService implements CacheService {

	private static Logger log = LoggerFactory.getLogger(JedisCacheService.class);

	private final Jedis jedis;

	public JedisCacheService(Jedis jedis) {
		super();
		this.jedis = jedis;
	}

	@Override
	public boolean existed(String key) {
		if (StringUtils.isBlank(key)) {
			throw new NullPointerException("请检查传入的key值");
		}
		synchronized (jedis) {
			return jedis.exists(key);
		}
	}

	@Override
	public void remove(String key) {
		if (StringUtils.isBlank(key)) {
			throw new NullPointerException("请检查传入的key值");
		}
		synchronized (jedis) {
			jedis.del(key);
		}
	}

	@Override
	public void setObject(String key, Object value, Long expiredMillisecond) {
		if (StringUtils.isBlank(key) || value == null) {
			throw new NullPointerException("请检查传入参数");
		}

		Gson g = new Gson();
		String valueStr = null;
		String statusCode = null;
		valueStr = g.toJson(value);

		// 如果keepTime值为null或keepTime值小于等于0，那么都按永久生效处理
		if (expiredMillisecond == null || expiredMillisecond <= 0L) {
			synchronized (jedis) {
				statusCode = jedis.set(key, valueStr);
			}
		} else {
			// NX|XX, NX -- Only set the key if it does not already exist. XX --
			// Only set the key if it already exist.
			// EX|PX, expire time units: EX = seconds; PX = milliseconds
			// time expire time in the units of <code>expx</code>
			synchronized (jedis) {
				statusCode = jedis.set(key, valueStr, "null", "px", expiredMillisecond);
			}
		}
		if (log.isDebugEnabled()) {
			log.debug("key=[{}]键保存成功{}，value=[{}]", key, statusCode, valueStr);
		}
	}

	@Override
	public <T> T getObject(String key, Class<T> clazz) throws BusinessException {
		if (StringUtils.isBlank(key) || clazz == null) {
			throw new NullPointerException("请检查传入参数");
		}

		Gson g = new Gson();
		T t = null;
		String value = null;
		synchronized (jedis) {
			value = jedis.get(key);
		}
		if (!StringUtils.isBlank(value)) {
			try {
				t = (T) g.fromJson(value, clazz);
			} catch (JsonSyntaxException e) {
				throw new BusinessException(CommonErrorCode.JSON_ERROR, e).put("methodName", "getObject")
						.put("key", key).put("clazz", clazz).put("value", value)
						.setMessage("${methodName}方法, key=${key}, value=${value}, 解析json串失败: " + e.getMessage());
			}
		}
		return t;
	}

	@Override
	public void setHashEntry(String key, String entryKey, Object entryValue, Long expiredMillisecond) {
		if (StringUtils.isBlank(key) || StringUtils.isBlank(entryKey) || entryValue == null) {
			throw new NullPointerException("请检查传入参数");
		}

		Gson g = new Gson();
		String valueStr = g.toJson(entryValue);

		synchronized (jedis) {
			jedis.hset(key, entryKey, valueStr);
		}

		// 如果keepTime值为null或keepTime值小于等于0，那么都按永久生效处理
		if (expiredMillisecond != null && expiredMillisecond > 0L) {
			synchronized (jedis) {
				jedis.expire(key, expiredMillisecond.intValue() / 1000);
			}
		}
	}

	@Override
	public <T> T getHashEntry(String key, String entryKey, Class<T> clazz) throws BusinessException {

		if (StringUtils.isBlank(key) || StringUtils.isBlank(entryKey) || clazz == null) {
			throw new NullPointerException("请检查传入参数");
		}

		Gson g = new Gson();
		T t = null;
		String value = null;
		synchronized (jedis) {
			value = jedis.hget(key, entryKey);
		}
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
		if (StringUtils.isBlank(key) || clazz == null) {
			throw new NullPointerException("请检查传入参数");
		}

		Gson g = new Gson();
		Map<String, String> tmpMap = new HashMap<String, String>();
		for (Map.Entry<String, T> entry : map.entrySet()) {
			tmpMap.put(entry.getKey(), g.toJson(entry.getValue(), clazz));
		}
		synchronized (jedis) {
			jedis.hmset(key, tmpMap);
		}
	}

	@Override
	public <T> Map<String, T> getHashMap(String key, Class<T> clazz) throws BusinessException {
		if (StringUtils.isBlank(key) || clazz == null) {
			throw new NullPointerException("请检查传入参数");
		}

		Gson g = new Gson();
		Map<String, T> returnMap = new HashMap<String, T>();
		Map<String, String> tmpMap = null;
		synchronized (jedis) {
			tmpMap = jedis.hgetAll(key);
		}
		if (tmpMap != null) {
			for (Map.Entry<String, String> entry : tmpMap.entrySet()) {
				try {
					returnMap.put(entry.getKey(), g.fromJson(entry.getValue(), clazz));
				} catch (JsonSyntaxException e) {
					throw new BusinessException(CommonErrorCode.JSON_ERROR, e).put("methodName", "getHashMap")
							.put("key", key).put("clazz", clazz).put("value", entry.getValue())
							.setMessage("${methodName}方法, key=${key}, value=${value}, 解析json串失败: " + e.getMessage());
				}
			}
		}
		return returnMap;
	}

	@Override
	public <T> boolean sendMessage(String channel, T message) throws BusinessException {
		return false;
	}

	@Override
	public <T> T deserialize(byte[] bytes) {
		return null;
	}

	@Override
	public boolean setObjectIfAbsent(String key, Object value, Long expiredMillisecond) {
		return setObjectNXXX(key, value, expiredMillisecond, "nx");
	}

	private boolean setObjectNXXX(String key, Object value, Long expiredMillisecond, String nxxx) {
		if (StringUtils.isBlank(key) || value == null) {
			throw new NullPointerException("请检查传入参数");
		}

		Gson g = new Gson();
		String valueStr = null;
		String statusCode = null;
		valueStr = g.toJson(value);

		// NX|XX, NX -- Only set the key if it does not already exist. XX --
		// Only set the key if it already exist.
		// EX|PX, expire time units: EX = seconds; PX = milliseconds
		// time expire time in the units of <code>expx</code>
		synchronized (jedis) {
			statusCode = jedis.set(key, valueStr, nxxx, "px", expiredMillisecond);
		}
		if (log.isInfoEnabled()) {
			log.info("setObject, key = {}, value = {}, nxxx = {}, expiredMillisecond = {}", key, value, nxxx,
					expiredMillisecond);
		}

		return "OK".equalsIgnoreCase(statusCode);
	}

}
