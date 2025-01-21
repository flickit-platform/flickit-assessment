package org.flickit.assessment.core.application.domain;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class Attribute {

    private final long id;
    private final Integer index;
    private final String title;
    private final String description;
    private final int weight;

    /** This field is set when required (e.g., calculate) */
    @Nullable
    private final List<Question> questions;
}
