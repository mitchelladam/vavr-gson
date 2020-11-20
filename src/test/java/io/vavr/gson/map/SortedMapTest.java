package io.vavr.gson.map;

import java.lang.reflect.Type;

import com.google.gson.reflect.TypeToken;
import io.vavr.collection.Map;
import io.vavr.collection.SortedMap;
import io.vavr.collection.TreeMap;

public class SortedMapTest extends MapLikeTest<SortedMap<?, ?>> {

    @Override
    @SuppressWarnings("unchecked")
    SortedMap<?, ?> of(Object key, Object value) {
        return TreeMap.of((Comparable) key, value);
    }

    @Override
    @SuppressWarnings("unchecked")
    <K, V> Map<K, V> ofTyped(K key, V value) {
        if (key instanceof Comparable) {
            final Comparable typedKey = (Comparable<K>) key;
            final Map<?, ?> of = TreeMap.of(typedKey, value);
            return (Map<K, V>) of;
        } else throw new IllegalArgumentException("Must be Comparable");
    }

    @Override
    Class<?> clz() {
        return SortedMap.class;
    }

    @Override
    Type type() {
        return new TypeToken<SortedMap<String, Integer>>() {
        }.getType();
    }

    @Override
    Type typeWithNestedType() {
        return new TypeToken<SortedMap<String, SortedMap<String, Integer>>>() {
        }.getType();
    }

    @Override
    Type intType() {
        return new TypeToken<SortedMap<Integer, Integer>>() {
        }.getType();
    }

    @Override
    Type typeWithNestedIntType() {
        return new TypeToken<SortedMap<String, SortedMap<Integer, Integer>>>() {
        }.getType();
    }

    @Override
    Type getComplexNestedKeyType() {
        return new TypeToken<SortedMap<CustomKey, SortedMap<CustomKey, Integer>>>() {
        }.getType();
    }
}
