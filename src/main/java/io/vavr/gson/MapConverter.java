/*                        __    __  __  __    __  ___
 *                       \  \  /  /    \  \  /  /  __/
 *                        \  \/  /  /\  \  \/  /  /
 *                         \____/__/  \__\____/__/.ɪᴏ
 * ᶜᵒᵖʸʳᶦᵍʰᵗ ᵇʸ ᵛᵃᵛʳ ⁻ ˡᶦᶜᵉⁿˢᵉᵈ ᵘⁿᵈᵉʳ ᵗʰᵉ ᵃᵖᵃᶜʰᵉ ˡᶦᶜᵉⁿˢᵉ ᵛᵉʳˢᶦᵒⁿ ᵗʷᵒ ᵈᵒᵗ ᶻᵉʳᵒ
 */
package io.vavr.gson;

import java.lang.reflect.Type;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.Map;

class MapConverter<K, V, T extends Map<K, V>> extends JsonObjectConverter<T> {

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

    private Tuple2<K, ?> convert(JsonDeserializationContext ctx, java.util.Map.Entry<String, JsonElement> e, Type keyType) {
        final JsonPrimitive json = new JsonPrimitive(e.getKey());
        final K deserialize = ctx.deserialize(json, keyType);
        return Tuple.of(deserialize, e.getValue());
    }

    private Tuple2<K, V> convert(JsonDeserializationContext ctx, java.util.Map.Entry<String, JsonElement> e, Type keyType, Type valueType) {
        final JsonPrimitive json = new JsonPrimitive(e.getKey());
        final K deserialize = ctx.deserialize(json, keyType);
        final V deserialize1 = ctx.deserialize(e.getValue(), valueType);
        return Tuple.of(deserialize, deserialize1);
    }

    @Override
    Map<K, V> toMap(T src) {
        return src;
    }
}
