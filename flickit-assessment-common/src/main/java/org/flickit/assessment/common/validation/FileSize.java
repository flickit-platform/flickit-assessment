package org.flickit.assessment.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = FileSizeValidation.class)
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface FileSize {

    String message() default "Invalid file size";

    long maxSize() default 5 * 1024 * 1024; // Default size is 5MB

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
