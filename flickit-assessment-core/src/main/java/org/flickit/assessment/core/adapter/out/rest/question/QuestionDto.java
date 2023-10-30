package org.flickit.assessment.core.adapter.out.rest.question;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.flickit.assessment.core.adapter.out.rest.questionimpact.QuestionImpactDto;
import org.flickit.assessment.core.application.domain.Question;
import org.flickit.assessment.core.application.domain.QuestionImpact;

import java.util.List;

public record QuestionDto(Long id,
                          @JsonProperty("question_impacts")
                          List<QuestionImpactDto> questionImpacts) {

    public Question dtoToDomain() {
        List<QuestionImpact> impacts = questionImpacts.stream()
            .map(QuestionImpactDto::dtoToDomain)
            .toList();
        return new Question(id, impacts);
    }
}
