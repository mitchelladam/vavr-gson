package io.vavr.gson.map;

import java.lang.reflect.Type;

import com.google.gson.reflect.TypeToken;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;

public class MapTest extends MapLikeTest<Map<?, ?>> {

    @Override
    Map<?, ?> of(Object key, Object value) {
        return HashMap.of(key, value);
    }

    @Override
    Class<?> clz() {
        return Map.class;
    }

    @Override
    Type type() {
        return new TypeToken<Map<String, Integer>>() {
        }.getType();
    }

    @Override
    Type typeWithNestedType() {
        return new TypeToken<Map<String, Map<String, Integer>>>() {
        }.getType();
    }

    @Override
    Type intType() {
        return new TypeToken<Map<Integer, Integer>>() {
        }.getType();
    }

    @Override
    Type typeWithNestedIntType() {
        return new TypeToken<Map<String, Map<Integer, Integer>>>() {
        }.getType();
    }

    @Override
    Type getComplexNestedKeyType() {
        return new TypeToken<Map<CustomKey, Map<CustomKey, Integer>>>() {
        }.getType();
    }
}
