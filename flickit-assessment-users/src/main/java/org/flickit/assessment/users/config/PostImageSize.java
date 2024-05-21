package org.flickit.assessment.users.config;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ImageSizeValidation.class)
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface PostImageSize {

    String message() default "Invalid image size";

    long maxSize() default 5 * 1024 * 1024; // Default size is 5MB

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
