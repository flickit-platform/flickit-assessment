package org.flickit.flickitassessmentcore.adapter.out.rest.question;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.flickit.flickitassessmentcore.adapter.out.rest.questionimpact.QuestionImpactDto;
import org.flickit.flickitassessmentcore.domain.Question;
import org.flickit.flickitassessmentcore.domain.QuestionImpact;

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
