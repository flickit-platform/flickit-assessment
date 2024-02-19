package org.flickit.assessment.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RUNTIME)
@Constraint(validatedBy = EnumValueValidator.class)
public @interface EnumValue {

    Class<? extends Enum<?>> enumClass();

    Class<?>[] groups() default {};

    String message() default "must be any of enum {enumClass}";

    Class<? extends Payload>[] payload() default {};
}
