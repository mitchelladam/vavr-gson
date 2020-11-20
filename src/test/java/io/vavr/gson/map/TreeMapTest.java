package io.vavr.gson.map;

import java.lang.reflect.Type;

import com.google.gson.reflect.TypeToken;
import io.vavr.collection.TreeMap;

public class TreeMapTest extends MapLikeTest<TreeMap<?, ?>> {

    @Override
    @SuppressWarnings("unchecked")
    TreeMap<?, ?> of(Object key, Object value) {
        return TreeMap.of((Comparable) key, value);
    }

    @Override
    Class<?> clz() {
        return TreeMap.class;
    }

    @Override
    Type type() {
        return new TypeToken<TreeMap<String, Integer>>() {
        }.getType();
    }

    @Override
    Type typeWithNestedType() {
        return new TypeToken<TreeMap<String, TreeMap<String, Integer>>>() {
        }.getType();
    }

    @Override
    Type intType() {
        return new TypeToken<TreeMap<Integer, Integer>>() {
        }.getType();
    }

    @Override
    Type typeWithNestedIntType() {
        return new TypeToken<TreeMap<String, TreeMap<Integer, Integer>>>() {
        }.getType();
    }

    @Override
    Type getComplexNestedKeyType() {
        return new TypeToken<TreeMap<CustomKey, TreeMap<CustomKey, Integer>>>() {
        }.getType();
    }
}
