package com.terran4j.commons.util.value;

import java.util.HashMap;
import java.util.Map;

public class MapValueSource<K, V> implements ValueSource<K, V> {

	private final Map<K, V> map;

	public MapValueSource() {
		this(new HashMap<>());
	}

	public MapValueSource(Map<K, V> map) {
		super();
		this.map = map;
	}

	public MapValueSource<K, V> put(K key, V value) {
		map.put(key, value);
		return this;
	}

	@Override
	public V get(K key) {
		return map.get(key);
	}

}
