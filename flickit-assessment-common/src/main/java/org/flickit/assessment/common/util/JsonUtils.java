package org.flickit.assessment.common.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;

import java.util.Collections;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonUtils {

    public static final ObjectMapper objectMapper = new ObjectMapper()
        .setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL)
        .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
        .registerModule(new JavaTimeModule())
        .disable(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
        .disable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS);

    @SneakyThrows
    public static String toJson(Object value) {
        return objectMapper.writeValueAsString(value);
    }

    @SneakyThrows
    public static <T> T fromJson(String value, Class<T> valueType) {
        return objectMapper.readValue(value, valueType);
    }

    @SneakyThrows
    public static <V> Map<KitLanguage, V> toTranslations(String translations, Class<V> translationsType) {
        if (translations == null) {
            return Collections.emptyMap();
        }
        var type = objectMapper.getTypeFactory().constructMapType(Map.class, KitLanguage.class, translationsType);
        return objectMapper.readValue(translations, type);
    }
}
