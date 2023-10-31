package org.flickit.assessment.core.adapter.out.rest.answeroption;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.flickit.assessment.core.adapter.out.rest.answeroptionimpact.AnswerOptionImpactDto;
import org.flickit.assessment.core.application.domain.AnswerOption;
import org.flickit.assessment.core.application.domain.AnswerOptionImpact;

import java.util.List;

public record AnswerOptionDto(Long id,
                              @JsonProperty("question_id")
                              Long questionId,
                              @JsonProperty("answer_option_impacts")
                              List<AnswerOptionImpactDto> answerOptionImpacts) {

    public AnswerOption dtoToDomain() {
        List<AnswerOptionImpact> impacts = answerOptionImpacts.stream()
            .map(AnswerOptionImpactDto::dtoToDomain)
            .toList();
        return new AnswerOption(id, questionId, impacts);
    }
}
