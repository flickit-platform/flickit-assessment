package org.flickit.assessment.kit.application.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class QuestionImpact {

    private final Long id;
    private final long attributeId;
    private final long maturityLevelId;
    private final int weight;
    private final Long questionId;
    @Setter
    private List<AnswerOptionImpact> optionImpacts;
}
