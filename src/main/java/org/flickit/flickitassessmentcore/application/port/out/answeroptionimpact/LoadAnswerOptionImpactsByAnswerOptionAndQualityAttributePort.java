package org.flickit.flickitassessmentcore.application.port.out.answeroptionimpact;

import org.flickit.flickitassessmentcore.domain.AnswerOptionImpact;

import java.util.Set;

public interface LoadAnswerOptionImpactsByAnswerOptionAndQualityAttributePort {

    Result loadByAnswerOptionIdAndQualityAttributeId(Long answerOptionId, Long qualityAttributeId);

    record Result(Set<AnswerOptionImpact> optionImpacts) {}
}
