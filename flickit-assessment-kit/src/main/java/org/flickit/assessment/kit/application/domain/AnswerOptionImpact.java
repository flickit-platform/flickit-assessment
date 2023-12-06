package org.flickit.assessment.kit.application.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AnswerOptionImpact {

    private final Long id;
    private final long optionId;
    private final double value;
}
