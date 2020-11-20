package io.vavr.gson.map;

import java.lang.reflect.Type;

import com.google.gson.reflect.TypeToken;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import org.junit.Test;

public class HashMapTest extends MapLikeTest<HashMap<?, ?>> {

    @Override
    HashMap<?, ?> of(Object key, Object value) {
        return HashMap.of(key, value);
    }

    @Override
    <K, V> Map<K, V> ofTyped(K key, V value) {
        return HashMap.of(key, value);
    }

    @Override
    Class<?> clz() {
        return HashMap.class;
    }

    @Override
    Type type() {
        return new TypeToken<HashMap<String, Integer>>() {
        }.getType();
    }

    @Override
    Type intType() {
        return new TypeToken<HashMap<Integer, Integer>>() {
        }.getType();
    }

    @Override
    Type typeWithNestedType() {
        return new TypeToken<HashMap<String, HashMap<String, Integer>>>() {
        }.getType();
    }

    @Override
    Type typeWithNestedIntType() {
        return new TypeToken<HashMap<String, HashMap<Integer, Integer>>>() {
        }.getType();
    }

    @Override
    Type getComplexNestedKeyType() {
        return new TypeToken<HashMap<CustomKey, HashMap<CustomKey, Integer>>>() {
        }.getType();
    }

    @Test
    public void deserializeLongKey() {
        Map<Long, Integer> map = gson.fromJson("{\"1\":2}", new TypeToken<HashMap<Long, Integer>>(){}.getType());
        assert clz().isAssignableFrom(map.getClass());
        assert map.get(1L).get() == 2;
    }

    @Test
    public void deserializeDoubleKey() {
        Map<Double, Integer> map = gson.fromJson("{\"1.323\":2}", new TypeToken<HashMap<Double, Integer>>(){}.getType());
        assert clz().isAssignableFrom(map.getClass());
        assert map.get(1.323D).get() == 2;
    }
}
