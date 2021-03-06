package io.vavr.gson.map;

import java.lang.reflect.Type;

import com.google.gson.reflect.TypeToken;
import io.vavr.collection.LinkedHashMap;
import io.vavr.collection.Map;

public class LinkedHashMapTest extends MapLikeTest<LinkedHashMap<?, ?>> {

    @Override
    LinkedHashMap<?, ?> of(Object key, Object value) {
        return LinkedHashMap.of(key, value);
    }

    @Override
    <K, V> Map<K, V> ofTyped(K key, V value) {
        return LinkedHashMap.of(key, value);
    }

    @Override
    Class<?> clz() {
        return LinkedHashMap.class;
    }

    @Override
    Type type() {
        return new TypeToken<LinkedHashMap<String, Integer>>() {
        }.getType();
    }

    @Override
    Type typeWithNestedType() {
        return new TypeToken<LinkedHashMap<String, LinkedHashMap<String, Integer>>>() {
        }.getType();
    }

    @Override
    Type intType() {
        return new TypeToken<LinkedHashMap<Integer, Integer>>() {
        }.getType();
    }

    @Override
    Type typeWithNestedIntType() {
        return new TypeToken<LinkedHashMap<String, LinkedHashMap<Integer, Integer>>>() {
        }.getType();
    }

    @Override
    Type getComplexNestedKeyType() {
        return new TypeToken<LinkedHashMap<CustomKey, LinkedHashMap<CustomKey, Integer>>>() {
        }.getType();
    }
}
