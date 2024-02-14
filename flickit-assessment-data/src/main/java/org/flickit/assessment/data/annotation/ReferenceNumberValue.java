package org.flickit.assessment.data.annotation;

import org.hibernate.annotations.ValueGenerationType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@ValueGenerationType(generatedBy = ReferenceNumberValueGeneration.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface ReferenceNumberValue {

    String query();
}
