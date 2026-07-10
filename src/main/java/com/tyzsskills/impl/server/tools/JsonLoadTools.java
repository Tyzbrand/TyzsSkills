package com.tyzsskills.impl.server.tools;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class JsonLoadTools {
    public static @Nullable <T> T getSafeElement(JsonObject obj, String key, Function<JsonPrimitive, T> mapper){
        if(obj == null ||key == null) return null;

        var value = obj.get(key);
        if(value == null || !value.isJsonPrimitive()) return null;

        return mapper.apply(value.getAsJsonPrimitive());
    }

    public static @Nullable <T> List<T> getSafeList(JsonObject obj, String key, Function<JsonElement, T> mapper){
        if (obj == null || key == null || !obj.has(key)) return null;

        var element = obj.get(key);
        if (!element.isJsonArray()) return null;

        var array = element.getAsJsonArray();
        List<T> list = new ArrayList<>();

        try {
            for (var item : array) {
                list.add(mapper.apply(item));
            }
        } catch (Exception e){return null;}

        return list;
    }

    public static @Nullable <K, V> Map<K, V> getSafeMap(JsonObject obj, String key,
            Function<String, K> keyMapper, Function<JsonElement, V> valueMapper) {

        if (obj == null || key == null || !obj.has(key)) return null;

        var element = obj.get(key);
        if (!element.isJsonObject()) return null;

        var jsonMap = element.getAsJsonObject();
        Map<K, V> map = new HashMap<>();

        try {
            for (var entry : jsonMap.entrySet()) {
                K mappedKey = keyMapper.apply(entry.getKey());
                V mappedValue = valueMapper.apply(entry.getValue());

                if (mappedKey != null && mappedValue != null) {
                    map.put(mappedKey, mappedValue);
                }
            }
            return map;
        }
        catch (Exception e) {return null;}

    }

    public static @Nullable <T extends Enum<T>> T getSafeEnum(JsonObject obj, String key, Class<T> enumClass){
        if (obj == null || key == null || !obj.has(key)) return null;

        var element = obj.get(key);
        if (!element.isJsonPrimitive()) return null;

        try {return Enum.valueOf(enumClass, element.getAsString().toUpperCase());}
        catch (IllegalArgumentException e) {return null;}
    }

    public static @Nullable <T> T getSafeObject(JsonObject obj, String key, Function<JsonObject, T> mapper){
        if(obj == null ||key == null || !obj.has(key)) return null;

        var element = obj.get(key);
        if (!element.isJsonObject()) return null;

        try {
            return mapper.apply(element.getAsJsonObject());
        } catch (Exception e) {
            return null;
        }
    }
}
