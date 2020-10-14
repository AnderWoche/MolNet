package de.moldiy.molnet.utils;

import java.util.*;


/**
 * 
 * @author Moldiy (Humann)
 *
 */

public class BothSideHashMapWithArray<K, V> {
	
	private final Map<K, V> map;
	private final Map<V, K> invertedMap;
	private final List<V> array;
	
	public BothSideHashMapWithArray(boolean synchronised) {
		if(synchronised) {
			this.map = Collections.synchronizedMap(new HashMap<>());
			this.invertedMap = Collections.synchronizedMap(new HashMap<>());
			this.array = Collections.synchronizedList(new ArrayList<>());
		} else {
			this.map = new HashMap<>();
			this.invertedMap = new HashMap<>();
			this.array = new ArrayList<>();
		}
	}

	public V getValue(K key) {
		return map.get(key);
	}
	
	public K getKey(V value) {
		return invertedMap.get(value);
	}

	public void put(K key, V value) {
		this.map.put(key, value);
		this.invertedMap.put(value, key);
		this.array.add(value);
	}

	public boolean containsKey(K key) {
		return this.map.containsKey(key);
	}
	
	public boolean containsValue(V value) {
		return this.map.containsValue(value);
	}

	public V removeWithKey(K key) {
		V value = this.map.remove(key);
		this.invertedMap.remove(value);
		this.array.remove(value);
		return value;
	}
	
	public K removeWithValue(V value) {
		K key = this.invertedMap.remove(value);
		this.map.remove(key);
		this.array.remove(value);
		return key;
	}
	
	public boolean isEmpty() {
		if(!this.map.isEmpty()) {
			return false;
		}
		if(!this.invertedMap.isEmpty()) {
			return false;
		}
		return this.array.size() <= 0;
	}
	
	public void clear() {
		this.map.clear();
		this.invertedMap.clear();
		this.array.clear();
	}

	public List<V> getValueArray() {
		return this.array;
	}
	
}
