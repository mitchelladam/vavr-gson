/*                        __    __  __  __    __  ___
 *                       \  \  /  /    \  \  /  /  __/
 *                        \  \/  /  /\  \  \/  /  /
 *                         \____/__/  \__\____/__/.ɪᴏ
 * ᶜᵒᵖʸʳᶦᵍʰᵗ ᵇʸ ᵛᵃᵛʳ ⁻ ˡᶦᶜᵉⁿˢᵉᵈ ᵘⁿᵈᵉʳ ᵗʰᵉ ᵃᵖᵃᶜʰᵉ ˡᶦᶜᵉⁿˢᵉ ᵛᵉʳˢᶦᵒⁿ ᵗʷᵒ ᵈᵒᵗ ᶻᵉʳᵒ
 */
package io.vavr.gson;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.Map;
import io.vavr.collection.Traversable;
import io.vavr.collection.Vector;

class MapConverter<K, V, T extends Map<K, V>> extends MapTypeConverter<T> {

    private final Function<Iterable<Tuple2<K, V>>, Map<K, V>> factory;

    MapConverter(Function<Iterable<Tuple2<K, V>>, Map<K, V>> factory) {
        this.factory = factory;
    }

    @Override
    @SuppressWarnings("unchecked")
    T fromJsonObject(JsonObject obj, Type type, Type[] subTypes, JsonDeserializationContext ctx) throws JsonParseException {
        Function<java.util.Map.Entry<String, JsonElement>, Tuple2<K, V>> mapper;
        if (subTypes.length == 2) {
            mapper = e -> convert(ctx, e, subTypes[0], subTypes[1]);
        } else {
            mapper = e -> Tuple.of((K) e.getKey(), (V) e.getValue());
        }
        final Set<java.util.Map.Entry<String, JsonElement>> entries = obj.entrySet();
        final Iterable<Tuple2<K, V>> collect = entries.stream().map(mapper).collect(Collectors.toList());
        return (T) factory.apply(collect);
    }

    @Override
    @SuppressWarnings("unchecked")
    T fromJsonArray(JsonArray arr, Type type, Type[] subTypes, JsonDeserializationContext ctx) throws JsonParseException {
        final Awkward tuple2KV = new Awkward(Tuple2.class, subTypes);
        final Type traversableType = new TypeToken<Traversable<?>>() {
        }.getType();
        final Awkward traversableTuple2KV = new Awkward(traversableType, tuple2KV);
        final TraversableConverter<Traversable<Tuple2<K, V>>> traversableConverter = new TraversableConverter<>(Vector::ofAll);
        final Traversable<Tuple2<K, V>> tuple2s = traversableConverter.fromJsonArray(arr, traversableTuple2KV, traversableTuple2KV.getActualTypeArguments(), ctx);
        return (T) factory.apply(tuple2s);
    }

    private Tuple2<K, V> convert(JsonDeserializationContext ctx, java.util.Map.Entry<String, JsonElement> e, Type keyType, Type valueType) {
        final JsonPrimitive json = new JsonPrimitive(e.getKey());
        final K key = ctx.deserialize(json, keyType);
        final V value = ctx.deserialize(e.getValue(), valueType);
        return Tuple.of(key, value);
    }

    @Override
    Map<K, V> toMap(T src) {
        return src;
    }

    private static final class Awkward implements ParameterizedType {

        private final Type rawType;
        private final Type[] subTypes;

        private Awkward(Type rawType, Type... subTypes) {
            this.rawType = rawType;
            this.subTypes = subTypes;
        }

        @Override
        public Type[] getActualTypeArguments() {
            return subTypes;
        }

        @Override
        public Type getRawType() {
            return rawType;
        }

        @Override
        public Type getOwnerType() {
            return null;
        }
    }
}
