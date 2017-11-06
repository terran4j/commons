package com.terran4j.mock.rediscow;

import com.terran4j.commons.rediscow.cache.CacheService;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MockCacheService implements CacheService {

	private final Map<String, Object> cache = new ConcurrentHashMap<>();

	@Override
	public boolean existed(String key) {
		return cache.containsKey(key);
	}

	@Override
	public void remove(String key) {
		cache.remove(key);
	}

	@Override
	public void setObject(String key, Object value, Long keepTime) {
		cache.put(key, value);
	}
	
	@Override
	public synchronized boolean setObjectIfAbsent(String key, Object value, Long keepTime) {
		if (cache.containsKey(key)) {
			return false;
		}
		setObject(key, value, keepTime);
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getObject(String key, Class<T> clazz) {
		return (T) cache.get(key);
	}

	@Override
	public void setHashEntry(String key, String entryKey, Object entryValue, Long keepTime) {
		Object value = cache.get(entryKey);
		if (value instanceof Map) {
			@SuppressWarnings({ "rawtypes", "unchecked" })
			Map<String, Object> map = (Map<String, Object>) (Map) value;
			map.put(entryKey, entryValue);
		} else {
			Map<String, Object> map = new ConcurrentHashMap<>();
			map.put(entryKey, entryValue);
			cache.put(key, map);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <T> T getHashEntry(String key, String entryKey, Class<T> clazz) {
		Object value = cache.get(entryKey);
		if (value instanceof Map) {
			return (T) ((Map) value).get(entryKey);
		}
		return null;
	}

	@Override
	public <T> void setHashMap(String key, Map<String, T> map, Class<T> clazz) {
		cache.put(key, map);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> Map<String, T> getHashMap(String key, Class<T> clazz) {
		return (Map<String, T>) cache.get(key);
	}

}
