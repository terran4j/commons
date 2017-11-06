package com.terran4j.commons.util.value;

public interface ValueSource<K, V> {

	V get(K key);
	
}
