package com.terran4j.commons.util.value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 带索引的列表对象，其特点如下：<br>
 * 1. 插入修改快速，删除慢。<br>
 * 2. 既保持了列表的有序性，也能根据一个key快速找到对应的value。<br>
 * 3. 本类是非线性安全的，请使用者自行保证它的线性安全性。<br>
 * 4. 不允许容纳重复的元素。
 */
public final class KeyedList<K, V> implements ValueSource<K, V> {

	private final List<K> list = new ArrayList<K>();

	private final Map<K, V> map = new HashMap<K, V>();

	public KeyedList() {
		super();
	}

	@Override
	public V get(K key) {
		return map.get(key);
	}

	public void clear() {
		list.clear();
		map.clear();
	}

	public boolean containsKey(K key) {
		return map.containsKey(key);
	}

	public int getIndex(K key) {
		if (key == null) {
			return -1;
		}
		return list.indexOf(key);
	}

	private void checkNotNull(K key) {
		if (key == null) {
			throw new NullPointerException("key is null.");
		}
	}

	public void add(K key, V value, int index) {
		checkNotNull(key);
		if (containsKey(key)) {
			throw new RuntimeException("key[" + key + "] already existed of value: " + value);
		}
		map.put(key, value);
		list.add(index, key);
	}

	public void addOrUpdate(K key, V value) {
		checkNotNull(key);
		if (containsKey(key)) {
			map.put(key, value);
		} else {
			add(key, value, size());
		}
	}

	public void add(K key, V value) {
		add(key, value, size());
	}

	public V getByKey(K key) {
		if (key == null) {
			return null;
		}
		return map.get(key);
	}

	public V removeByKey(K key) {
		if (key == null) {
			return null;
		}
		V value = map.remove(key);
		list.remove(key);
		return value;
	}

	public int size() {
		return list.size();
	}

	public V get(int index) {
		if (index < 0 || index >= list.size()) {
			return null;
		}

		K key = list.get(index);
		if (key == null) {
			return null;
		}

		return map.get(key);
	}

	public K getKey(int index) {
		return list.get(index);
	}

	public V remove(int index) {
		K key = getKey(index);
		if (key != null) {
			list.remove(index);
			return map.remove(key);
		}
		return null;
	}

	public Set<K> keySet() {
		return map.keySet();
	}

	public List<V> getAll() {
		List<V> all = new ArrayList<V>();
		for (K key : list) {
			V item = map.get(key);
			all.add(item);
		}
		return all;
	}

	@Override
	public KeyedList<K, V> clone() {
		KeyedList<K, V> copy = new KeyedList<K, V>();
		for (K key : list) {
			copy.list.add(key);
			copy.map.put(key, map.get(key));
		}
		return copy;
	}

	public KeyedList<K, V> deduct(KeyedList<K, V> other) {
		if (other == null || other.size() == 0) {
			return clone();
		}

		KeyedList<K, V> copy = new KeyedList<K, V>();
		for (K key : list) {
			if (!other.containsKey(key)) {
				copy.add(key, getByKey(key));
			}
		}
		return copy;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (K key : list) {
			sb.append(key).append(" = ").append(map.get(key)).append("\n");
		}
		return sb.toString();
	}

}
