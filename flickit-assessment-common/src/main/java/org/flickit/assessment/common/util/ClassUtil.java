package org.flickit.assessment.common.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;

import java.util.Map;

@UtilityClass
public class ClassUtil {

    public static <T> int countAllFields(Class<T> clazz) {
        return clazz.getDeclaredFields().length;
    }

    public static <T> int countProvidedFields(T instance) {
        Map<String, Object> notNullFields = new ObjectMapper().convertValue(instance, new TypeReference<>() {
        });

        return (int) notNullFields.entrySet()
            .stream()
            .filter(entry -> !(entry.getValue().toString().isBlank()))
            .count();
    }
}
