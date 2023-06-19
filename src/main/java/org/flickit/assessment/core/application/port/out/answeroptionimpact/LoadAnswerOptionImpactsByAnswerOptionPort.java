package org.flickit.assessment.core.application.port.out.answeroptionimpact;

import org.flickit.assessment.core.domain.AnswerOptionImpact;

import java.util.Set;

public interface LoadAnswerOptionImpactsByAnswerOptionPort {
    Set<AnswerOptionImpact> findAnswerOptionImpactsByAnswerOptionId(Long answerOptionId);
}
