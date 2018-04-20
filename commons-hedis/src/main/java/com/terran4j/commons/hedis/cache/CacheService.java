package com.terran4j.commons.hedis.cache;

import java.util.Map;

import com.terran4j.commons.util.error.BusinessException;

/**
 * key - value 结构的缓存服务接口。<br>
 * 具体实现可能有多种，如：本地内存缓存、Redis 分布式缓存等。<br>
 * @author wei.jiang
 */
public interface CacheService {
	
	/**
	 * 是否存在缓存的key。
	 * @param key
	 * @return
	 */
	boolean existed(String key);
	
	/**
	 * 根据 key 删除缓存。
	 * @param key
	 */
	void remove(String key);

	/**
	 * 存一个键值对，不管键存不存在都能写成功。
	 * @param key 键
	 * @param value 值
	 * @param expiredMillisecond 过期时间，以毫秒为单位。
	 */
	void setObject(String key, Object value, Long expiredMillisecond);
	
	/**
	 * 存一个键值对，只有键不存在才能写成功。
	 * @param key 键
	 * @param value 值
	 * @param expiredMillisecond 过期时间，以毫秒为单位，不可以为空。
	 * @return true表示键不存在且写成功， false表示键存在且不写入。
	 */
	boolean setObjectIfAbsent(String key, Object value, Long expiredMillisecond);

	/**
	 * 根据键，获取值对象。
	 * @param key 键
	 * @param clazz 值的类型。
	 * @return 值对象
	 */
	<T> T getObject(String key, Class<T> clazz) throws BusinessException;
	
	/**
	 * 在一个 Hash 中设置键值对。
	 * @param key Hash对象本身的键
	 * @param entryKey Hash对象里面的键。
	 * @param entryValue Hash对象里面的值。
	 * @param expiredMillisecond 过期时间，以毫秒为单位。
	 */
	void setHashEntry(String key, String entryKey, Object entryValue, Long expiredMillisecond);
	
	/**
	 * 根据键，获取一个 Hash 中的值对象。
	 * @param key Hash对象本身的键
	 * @param entryKey Hash对象里面的键。
	 * @param clazz 值的类型。
	 */
	<T> T getHashEntry(String key, String entryKey, Class<T> clazz) throws BusinessException;
	
	/**
	 * 设置一个整体的 Hash 对象。
	 * @param key Hash对象本身的键
	 * @param map Hash对象。
	 * @param clazz Hash对象中值的类型。
	 */
	<T> void setHashMap(String key, Map<String, T> map, Class<T> clazz);
	
	/**
	 * 获取一个整体的 Map 对象。
	 * @param key Map对象本身的键
	 * @param clazz Map对象中值的类型。
	 * @return Map 对象。
	 */
	<T> Map<String, T> getHashMap(String key, Class<T> clazz) throws BusinessException;
	
}
