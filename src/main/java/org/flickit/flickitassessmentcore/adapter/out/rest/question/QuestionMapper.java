package org.flickit.flickitassessmentcore.adapter.out.rest.question;

import org.flickit.flickitassessmentcore.adapter.out.rest.questionImpact.QuestionImpactMapper;
import org.flickit.flickitassessmentcore.domain.Question;

import java.util.stream.Collectors;

public class QuestionMapper {

    public static Question toDomainModel(QuestionRestAdapter.QuestionDto dto) {
        return new Question(
            dto.id(),
            dto.title(),
            dto.questionImpacts().stream()
                .map(QuestionImpactMapper::toDomainModel)
                .collect(Collectors.toList())
        );
    }
}
