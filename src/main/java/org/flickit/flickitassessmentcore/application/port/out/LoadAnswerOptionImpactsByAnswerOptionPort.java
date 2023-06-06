package org.flickit.flickitassessmentcore.application.port.out;

import org.flickit.flickitassessmentcore.domain.AnswerOptionImpact;

import java.util.Set;

public interface LoadAnswerOptionImpactsByAnswerOptionPort {
    Set<AnswerOptionImpact> findAnswerOptionImpactsByAnswerOption(Long answerOptionId);
}
