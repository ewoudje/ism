package com.ewoudje.ism.util.data;

import org.jspecify.annotations.NonNull;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class SillyMap<K, V> extends AbstractMap<K, V> implements Map.Entry<K, V> {
    private final K key;
    private final V value;

    public SillyMap(K key, V value) {
        this.key = key;
        this.value = value;
    }

    @Override public void clear() { throw uoe(); }
    @Override public V compute(K key, BiFunction<? super K,? super V,? extends V> rf) { throw uoe(); }
    @Override public V computeIfAbsent(K key, Function<? super K,? extends V> mf) { throw uoe(); }
    @Override public V computeIfPresent(K key, BiFunction<? super K,? super V,? extends V> rf) { throw uoe(); }
    @Override public V merge(K key, V value, BiFunction<? super V,? super V,? extends V> rf) { throw uoe(); }
    @Override public V put(K key, V value) { throw uoe(); }
    @Override public void putAll(Map<? extends K,? extends V> m) { throw uoe(); }
    @Override public V putIfAbsent(K key, V value) { throw uoe(); }
    @Override public V remove(Object key) { throw uoe(); }
    @Override public boolean remove(Object key, Object value) { throw uoe(); }
    @Override public V replace(K key, V value) { throw uoe(); }
    @Override public boolean replace(K key, V oldValue, V newValue) { throw uoe(); }
    @Override public void replaceAll(BiFunction<? super K,? super V,? extends V> f) { throw uoe(); }
    @Override public V setValue(V value) { throw uoe(); }

    private UnsupportedOperationException uoe() {
        return new UnsupportedOperationException();
    }

    /**
     * @implNote {@code null} values are disallowed in these immutable maps,
     * so we can improve upon the default implementation since a
     * {@code null} return from {@code get(key)} always means the default
     * value should be returned.
     */
    @Override
    public V getOrDefault(Object key, V defaultValue) {
        V v;
        return ((v = get(key)) != null)
                ? v
                : defaultValue;
    }

    @Override
    public V get(Object key) {
        if (key == this.key) return value;
        if (this.key != null && this.key.equals(key)) return value;
        return null;
    }

    @Override
    public @NonNull Set<Entry<K, V>> entrySet() {
        return Set.of(this);
    }

    @Override
    public void forEach(BiConsumer<? super K, ? super V> action) {
        action.accept(key, value);
    }

    @Override
    public K getKey() {
        return key;
    }

    @Override
    public V getValue() {
        return value;
    }
}
