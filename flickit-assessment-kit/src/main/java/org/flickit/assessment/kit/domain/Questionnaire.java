package org.flickit.assessment.kit.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Questionnaire {

    @JsonIgnore
    private final Long id;

    private final String code;

    private final String title;

    private final String description;

    private final int index;
}
