/*                        __    __  __  __    __  ___
 *                       \  \  /  /    \  \  /  /  __/
 *                        \  \/  /  /\  \  \/  /  /
 *                         \____/__/  \__\____/__/.ɪᴏ
 * ᶜᵒᵖʸʳᶦᵍʰᵗ ᵇʸ ᵛᵃᵛʳ ⁻ ˡᶦᶜᵉⁿˢᵉᵈ ᵘⁿᵈᵉʳ ᵗʰᵉ ᵃᵖᵃᶜʰᵉ ˡᶦᶜᵉⁿˢᵉ ᵛᵉʳˢᶦᵒⁿ ᵗʷᵒ ᵈᵒᵗ ᶻᵉʳᵒ
 */
package io.vavr.gson;

import com.google.gson.*;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.Map;

import java.lang.reflect.Type;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class MapConverter<K, V, T extends Map<K, V>> extends JsonObjectConverter<T> {

    private final Function<Iterable<Tuple2<K, ?>>, Map<?, ?>> factory;

    MapConverter(Function<Iterable<Tuple2<K, ?>>, Map<?, ?>> factory) {
        this.factory = factory;
    }

    @Override
    @SuppressWarnings("unchecked")
    T fromJsonObject(JsonObject obj, Type type, Type[] subTypes, JsonDeserializationContext ctx) throws JsonParseException {
        Function<java.util.Map.Entry<String, JsonElement>, Tuple2<K, ?>> mapper;
        if (subTypes.length == 2) {
            mapper = e -> convert(ctx, e, subTypes[0], subTypes[1]);
        } else {
            mapper = e -> Tuple.of(ctx.deserialize(new JsonPrimitive(e.getKey()), subTypes[0]), e.getValue().getClass());
        }
        final Set<java.util.Map.Entry<String, JsonElement>> entries = obj.entrySet();
        final Stream<Tuple2<K, ?>> tuple2Stream = entries.stream().map(mapper);
        final Iterable<Tuple2<K, ?>> collect = tuple2Stream.collect(Collectors.toList());
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
