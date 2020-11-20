/*                        __    __  __  __    __  ___
 *                       \  \  /  /    \  \  /  /  __/
 *                        \  \/  /  /\  \  \/  /  /
 *                         \____/__/  \__\____/__/.ɪᴏ
 * ᶜᵒᵖʸʳᶦᵍʰᵗ ᵇʸ ᵛᵃᵛʳ ⁻ ˡᶦᶜᵉⁿˢᵉᵈ ᵘⁿᵈᵉʳ ᵗʰᵉ ᵃᵖᵃᶜʰᵉ ˡᶦᶜᵉⁿˢᵉ ᵛᵉʳˢᶦᵒⁿ ᵗʷᵒ ᵈᵒᵗ ᶻᵉʳᵒ
 */
package io.vavr.gson;

import java.lang.reflect.Type;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import io.vavr.Function1;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.LinkedHashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.collection.Multimap;

class MultimapConverter<K, V, T extends Multimap<K, V>> extends MapTypeConverter<T> {

    private final Function<Iterable<Tuple2<String, ?>>, Multimap<?, ?>> factory;

    MultimapConverter(Function<Iterable<Tuple2<String, ?>>, Multimap<?, ?>> factory) {
        this.factory = factory;
    }

    @Override
    @SuppressWarnings("unchecked")
    T fromJsonObject(JsonObject obj, Type type, Type[] subTypes, JsonDeserializationContext ctx) throws JsonParseException {
        Function1<java.util.Map.Entry<String, JsonElement>, Stream<Tuple2<String, ?>>> mapper;
        if (subTypes.length == 2) {
            mapper = e -> StreamSupport.stream(e.getValue().getAsJsonArray().spliterator(), false).map(v -> Tuple.of(ctx.deserialize(new JsonPrimitive(e.getKey()), subTypes[0]), ctx.deserialize(v, subTypes[1])));
        } else {
            mapper = e -> StreamSupport.stream(e.getValue().getAsJsonArray().spliterator(), false).map(v -> Tuple.of(e.getKey(), v));
        }
        return (T) factory.apply(obj.entrySet().stream().flatMap(mapper).collect(Collectors.toList()));
    }

    @Override
    @SuppressWarnings("unchecked")
    T fromJsonArray(JsonArray arr, Type type, Type[] subTypes, JsonDeserializationContext ctx) throws JsonParseException {
        throw new UnsupportedOperationException("Array types not supported for Multimaps");
    }

    @Override
    @SuppressWarnings("unchecked")
    Map<K, List<V>> toMap(T src) {
        // TODO implement Multimap.asMap()
        return src.foldLeft(LinkedHashMap.empty(), (m, t) -> m.put(t._1, (m.get(t._1)).getOrElse(List::empty).append(t._2)));
    }
}
