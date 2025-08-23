package org.flickit.assessment.kit.adapter.in.rest.questionimpact;

public record CreateQuestionImpactRequestDto(Long attributeId,
                                             Long maturityLevelId,
                                             Long questionId,
                                             Integer weight) {
}
