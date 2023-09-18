package org.flickit.flickitassessmentcore.adapter.out.rest.question;

import org.flickit.flickitassessmentcore.application.domain.Question;

import java.util.ArrayList;

public class QuestionMapper {

    public static Question toDomainModel(ImpactfulQuestionDto impactfulQuestionDto) {
        return new Question(
            impactfulQuestionDto.id(),
            new ArrayList<>()
        );
    }
}
