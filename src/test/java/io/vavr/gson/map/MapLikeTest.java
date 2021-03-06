package io.vavr.gson.map;

import java.lang.reflect.Type;
import java.util.Comparator;
import java.util.Objects;

import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import io.vavr.gson.AbstractTest;
import org.junit.Test;

public abstract class MapLikeTest<T extends Map<?, ?>> extends AbstractTest {

    abstract T of(Object key, Object value);

    abstract <K,V> Map<K,V> ofTyped(K key, V value);

    abstract Class<?> clz();

    abstract Type type();

    abstract Type intType();

    abstract Type typeWithNestedType();

    abstract Type typeWithNestedIntType();

    abstract Type getComplexNestedKeyType();

    @Test(expected = JsonParseException.class)
    public void badJson() {
        gson.fromJson("1", type());
    }

    @Test
    public void serialize() {
        assert gson.toJson(of(1, 2)).equals("{\"1\":2}");
    }

    @Test
    public void deserializeSimpleType() {
        Object obj = gson.fromJson("{\"1\":2}", clz());
        assert clz().isAssignableFrom(obj.getClass());
        Map<?, ?> map = (Map<?, ?>) obj;
        assert map.head()._2 instanceof JsonPrimitive;
        assert ((JsonPrimitive) map.head()._2).getAsInt() == 2;
    }

    @Test
    public void deserialize() {
        Map<String, Integer> map = gson.fromJson("{\"1\":2}", type());
        assert clz().isAssignableFrom(map.getClass());
        assert map.get("1").get() == 2;
    }

    @Test
    public void deserializeIntegerKey() {
        Map<Integer, Integer> map = gson.fromJson("{\"1\":2}", intType());
        assert clz().isAssignableFrom(map.getClass());
        assert map.get(1).get() == 2;
    }

    @Test
    public void deserializeWithCast() {
        Map<String, Integer> map = gson.fromJson("{\"1\":\"2\"}", type());
        assert clz().isAssignableFrom(map.getClass());
        assert map.get("1").get() == 2;
    }

    @Test
    public void deserializeNested() {
        Map<String, Map<String, Integer>> map = gson.fromJson("{\"1\":{\"2\":3}}", typeWithNestedType());
        assert clz().isAssignableFrom(map.get("1").get().getClass());
        assert map.get("1").get().get("2").get() == 3;
    }

    @Test
    public void deserializeNestedIntegerKey() {
        Map<String, Map<Integer, Integer>> map = gson.fromJson("{\"1\":{\"2\":3}}", typeWithNestedIntType());
        assert clz().isAssignableFrom(map.get("1").get().getClass());
        assert map.get("1").get().get(2).get() == 3;
    }

    @Test
    public void deserializeNestedComplexKey() {
        final CustomKey innerKey = new CustomKey(3, 4);
        final CustomKey outerKey = new CustomKey(1, 2);
        Map<CustomKey, Map<CustomKey, Integer>> map = gson.fromJson("[[{\"a\":1,\"b\":2},[[{\"a\":3,\"b\":4},5]]]]", getComplexNestedKeyType());
        assert clz().isAssignableFrom(map.get(outerKey).get().getClass());
        assert map.get(outerKey).get().get(innerKey).get() == 5;
    }

    @Test
    public void serializeNestedComplexKey() {
        final CustomKey innerKey = new CustomKey(3, 4);
        final Map<CustomKey, Integer> innerMap = ofTyped(innerKey, 5);
        final CustomKey outerKey = new CustomKey(1, 2);
        final Map<CustomKey, Map<CustomKey, Integer>> vavrMap = ofTyped(outerKey, innerMap);
        Map<CustomKey, Map<CustomKey, Integer>> map = gson.fromJson(gson.toJson(vavrMap), getComplexNestedKeyType());
        assert clz().isAssignableFrom(map.get(outerKey).get().getClass());
        assert map.get(outerKey).get().get(innerKey).get() == 5;
    }

    static final class CustomKey implements Comparable<CustomKey> {

        private final int a;
        private final int b;

        public CustomKey(int a, int b) {
            this.a = a;
            this.b = b;
        }

        public int getA() {
            return a;
        }

        public int getB() {
            return b;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            CustomKey customKey = (CustomKey) o;
            return a == customKey.a &&
                   b == customKey.b;
        }

        @Override
        public int hashCode() {
            return Objects.hash(a, b);
        }

        @Override
        public int compareTo(CustomKey o) {
            return Comparator.comparingInt(CustomKey::getA)
                             .thenComparing(CustomKey::getB)
                             .compare(this, o);
        }
    }
}
