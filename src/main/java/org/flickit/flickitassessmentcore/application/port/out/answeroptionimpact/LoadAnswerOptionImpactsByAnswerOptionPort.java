package org.flickit.flickitassessmentcore.application.port.out.answeroptionimpact;

import org.flickit.flickitassessmentcore.domain.AnswerOptionImpact;

import java.util.Set;

public interface LoadAnswerOptionImpactsByAnswerOptionPort {
    Result findAnswerOptionImpactsByAnswerOptionId(Param param);

    record Param(Long answerOptionId) {}

    record Result(Set<AnswerOptionImpact> optionImpacts) {}
}
