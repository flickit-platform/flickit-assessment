package org.flickit.assessment.core.application.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AnswerOption {

    private final Long id;
    private final Integer index;
    private final String title;
    private final Double value;
}
