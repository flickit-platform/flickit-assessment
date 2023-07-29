package org.flickit.flickitassessmentcore.application.port.out.answeroptionimpact;

import org.flickit.flickitassessmentcore.domain.AnswerOptionImpact;

import java.util.List;

public interface LoadAnswerOptionImpactsByAnswerOptionAndQualityAttributePort {

    Result loadByAnswerOptionIdAndQualityAttributeId(Long answerOptionId, Long qualityAttributeId);

    record Result(List<AnswerOptionImpact> optionImpacts) {}
}
