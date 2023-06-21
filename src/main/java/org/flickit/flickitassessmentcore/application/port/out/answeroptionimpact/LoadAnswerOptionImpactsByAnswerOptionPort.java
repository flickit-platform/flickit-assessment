package org.flickit.flickitassessmentcore.application.port.out.answeroptionimpact;

import org.flickit.flickitassessmentcore.domain.AnswerOptionImpact;

import java.util.Set;

public interface LoadAnswerOptionImpactsByAnswerOptionPort {
    Set<AnswerOptionImpact> findAnswerOptionImpactsByAnswerOptionId(Long answerOptionId);
}
