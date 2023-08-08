package org.flickit.flickitassessmentcore.adapter.out.rest.question;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.flickit.flickitassessmentcore.adapter.out.rest.questionImpact.QuestionImpactDto;
import org.flickit.flickitassessmentcore.domain.calculate.Question;
import org.flickit.flickitassessmentcore.domain.calculate.QuestionImpact;

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
