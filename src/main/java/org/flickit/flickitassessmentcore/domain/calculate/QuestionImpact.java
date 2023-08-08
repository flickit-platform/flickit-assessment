package org.flickit.flickitassessmentcore.domain.calculate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Getter
@RequiredArgsConstructor
public class QuestionImpact {

    private final Long id;
    private final int weight;
    private final Long maturityLevelId;
}
