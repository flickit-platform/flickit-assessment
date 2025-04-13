package org.flickit.assessment.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;

import java.util.Collections;
import java.util.Map;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonUtils {

    public static final ObjectMapper objectMapper = new ObjectMapper()
        .configure(FAIL_ON_UNKNOWN_PROPERTIES, false);

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
