package org.flickit.assessment.advice.application.domain;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Attribute {

    private final long id;

    private final String title;

    @Nullable private final Integer weight;
}
