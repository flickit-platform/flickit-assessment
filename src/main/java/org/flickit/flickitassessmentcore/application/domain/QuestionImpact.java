package org.flickit.flickitassessmentcore.application.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class QuestionImpact {

    private final long id;
    private final int weight;
    private final long maturityLevelId;
}
