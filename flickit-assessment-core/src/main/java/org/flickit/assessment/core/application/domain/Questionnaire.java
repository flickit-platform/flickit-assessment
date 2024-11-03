package org.flickit.assessment.core.application.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
public class Questionnaire {

    private final Long id;
    private final String title;
}
