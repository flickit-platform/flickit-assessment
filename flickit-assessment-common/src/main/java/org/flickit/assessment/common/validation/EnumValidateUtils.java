package org.flickit.assessment.common.validation;

import org.apache.commons.lang3.EnumUtils;
import org.flickit.assessment.common.exception.ValidationException;

import java.util.Map;

import static java.util.stream.Collectors.toMap;

public class EnumValidateUtils {

    public static <E extends Enum<E>, V> Map<E, V> validateAndConvert(Map<String, V> input,
                                                                      Class<E> enumType,
                                                                      String errorMsg) {
        if (input == null || input.isEmpty())
            return Map.of();
        return input.entrySet().stream()
            .map(entry -> {
                E enumKey = EnumUtils.getEnum(enumType, entry.getKey());
                if (enumKey == null)
                    throw new ValidationException(errorMsg);
                return Map.entry(enumKey, entry.getValue());
            }).collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
