package org.flickit.flickitassessmentcore.adapter.out.rest.answeroption;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.flickit.flickitassessmentcore.adapter.out.rest.answeroptionimpact.AnswerOptionImpactDto;
import org.flickit.flickitassessmentcore.domain.calculate.AnswerOption;

import java.util.List;

public record AnswerOptionDto(Long id,
                              @JsonProperty("question_id")
                              Long questionId,
                              @JsonProperty("answer_option_impacts")
                              List<AnswerOptionImpactDto> answerOptionImpacts) {

    public AnswerOption dtoToDomain() {
        return AnswerOption.builder()
            .id(id)
            .questionId(questionId)
            .impacts(answerOptionImpacts.stream()
                .map(AnswerOptionImpactDto::dtoToDomain)
                .toList())
            .build();
    }
}
