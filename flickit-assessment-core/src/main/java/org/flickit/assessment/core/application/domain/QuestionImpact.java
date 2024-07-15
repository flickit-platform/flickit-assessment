package org.flickit.assessment.core.application.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class QuestionImpact {

    private final long id;
    private final int weight;
    private final long attributeId;
    private final long maturityLevelId;
}
