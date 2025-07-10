package org.flickit.assessment.advice.application.domain;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class Attribute {

    private final long id;

    private final String title;

    @Nullable private Integer weight;
}
