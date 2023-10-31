package org.flickit.assessment.core.adapter.out.rest.answeroptionimpact;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.flickit.assessment.core.adapter.out.rest.questionimpact.QuestionImpactDto;
import org.flickit.assessment.core.application.domain.AnswerOptionImpact;

public record AnswerOptionImpactDto(Long id,
                                    Double value,
                                    @JsonProperty("question_impact")
                                    QuestionImpactDto questionImpact) {

    public AnswerOptionImpact dtoToDomain() {
        return new AnswerOptionImpact(id, value, questionImpact.dtoToDomain());
    }
}
