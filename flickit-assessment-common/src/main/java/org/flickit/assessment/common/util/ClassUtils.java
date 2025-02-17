package org.flickit.assessment.common.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;

import java.util.Collection;
import java.util.Map;

@UtilityClass
public class ClassUtils {

    public static <T> int countAllFields(Class<T> clazz) {
        return clazz.getDeclaredFields().length;
    }

    public static <T> int countProvidedFields(T instance) {
        Map<String, Object> notNullFields = new ObjectMapper().convertValue(instance, new TypeReference<>() {
        });

        return (int) notNullFields.entrySet()
            .stream()
            .filter(entry -> !isMetadataEmpty(entry.getValue()))
            .count();
    }

    public static boolean isMetadataEmpty(Object object) {
        if (object == null) return true;

        return switch (object) {
            case String str -> str.isBlank();
            case Collection<?> collection -> collection.isEmpty();
            case Map<?, ?> map -> map.isEmpty();
            case Object[] array -> array.length == 0;
            default -> object.getClass().isArray() && java.lang.reflect.Array.getLength(object) == 0;
        };
    }
}
