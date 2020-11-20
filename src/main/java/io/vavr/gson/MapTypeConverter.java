/*                        __    __  __  __    __  ___
 *                       \  \  /  /    \  \  /  /  __/
 *                        \  \/  /  /\  \  \/  /  /
 *                         \____/__/  \__\____/__/.ɪᴏ
 * ᶜᵒᵖʸʳᶦᵍʰᵗ ᵇʸ ᵛᵃᵛʳ ⁻ ˡᶦᶜᵉⁿˢᵉᵈ ᵘⁿᵈᵉʳ ᵗʰᵉ ᵃᵖᵃᶜʰᵉ ˡᶦᶜᵉⁿˢᵉ ᵛᵉʳˢᶦᵒⁿ ᵗʷᵒ ᵈᵒᵗ ᶻᵉʳᵒ
 */
package io.vavr.gson;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import io.vavr.collection.Map;

import static jdk.nashorn.internal.runtime.JSType.isPrimitive;

abstract class MapTypeConverter<T> implements JsonSerializer<T>, JsonDeserializer<T> {

    private static final Type[] EMPTY_TYPES = new Type[0];

    abstract T fromJsonObject(JsonObject arr, Type type, Type[] subTypes, JsonDeserializationContext ctx) throws JsonParseException;

    abstract T fromJsonArray(JsonArray arr, Type type, Type[] subTypes, JsonDeserializationContext ctx) throws JsonParseException;

    abstract Map<?, ?> toMap(T src);

    @Override
    public T deserialize(JsonElement json, Type type, JsonDeserializationContext ctx) throws JsonParseException {
        if (json.isJsonObject()) {
            if (type instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) type;
                Type[] types = parameterizedType.getActualTypeArguments();
                return fromJsonObject(json.getAsJsonObject(), type, types, ctx);
            } else {
                return fromJsonObject(json.getAsJsonObject(), type, EMPTY_TYPES, ctx);
            }
        } else if (json.isJsonArray()) {
            // list of tuples
            if (type instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) type;
                Type[] types = parameterizedType.getActualTypeArguments();
                return fromJsonArray(json.getAsJsonArray(), parameterizedType, types, ctx);
            } else {
                throw new JsonParseException("unsupported non-parameterised types for complex map keys");
            }
        } else {
            throw new JsonParseException("object or array expected");
        }
    }

    @Override
    public JsonElement serialize(T src, Type type, JsonSerializationContext ctx) {
        final Map<?, ?> tuple2s = toMap(src);
        if (tuple2s.isEmpty()) {
            return new JsonObject();
        } else {
            // test the first element
            final Object o = tuple2s.head()._1();
            // primitives are fine
            if (isPrimitive(o)) {
                return tuple2s.foldLeft(new JsonObject(), (a, e) -> {
                    a.add(ctx.serialize(e._1).getAsString(), ctx.serialize(e._2));
                    return a;
                });
            } else {
                //complex object key serialisation
                return tuple2s.foldLeft(new JsonArray(), (a, e) -> {
                    a.add(ctx.serialize(e));
                    return a;
                });
            }
        }
    }
}
