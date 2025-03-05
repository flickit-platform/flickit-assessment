package org.flickit.assessment.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EnumValueValidator implements ConstraintValidator<EnumValue, Object> {

    private Set<String> acceptedValues;

    @Override
    public void initialize(EnumValue constraintAnnotation) {
        acceptedValues = Stream.of(constraintAnnotation.enumClass().getEnumConstants())
            .map(Enum::name)
            .collect(Collectors.toSet());
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext constraintValidatorContext) {
        switch (value) {
            case null -> {
                return true;
            }
            case CharSequence charSequence -> {
                return acceptedValues.contains(charSequence.toString());
            }
            case Collection<?> values -> {
                return acceptedValues.containsAll(values.stream()
                    .map(Object::toString)
                    .toList());
            }
            default -> {
                return false;
            }
        }
    }
}
