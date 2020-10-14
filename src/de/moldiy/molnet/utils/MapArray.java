package de.moldiy.molnet.utils;

import java.util.*;

public class MapArray<K, V> {

    private final HashMap<K, List<V>> hashMap = new HashMap<>();

    private void putOrCreateNewList(K key, V value) {
        List<V> list = this.hashMap.computeIfAbsent(key, k -> new ArrayList<>());
        list.add(value);
    }

    public int size() {
        return this.hashMap.size();
    }

    public boolean isEmpty() {
        return this.hashMap.isEmpty();
    }

    public boolean containsKey(K key) {
        return false;
    }

    public boolean containsValue(V value) {
        return false;
    }

    public List<V> get(K key) {
        return this.hashMap.get(key);
    }

    public List<V> put(K key, V value) {
        this.putOrCreateNewList(key, value);
        return null;
    }

    public List<V> remove(Object key) {
        return this.hashMap.remove(key);
    }

    public void clear() {
        this.hashMap.clear();
    }
}
