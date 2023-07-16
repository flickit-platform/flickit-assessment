package org.flickit.flickitassessmentcore.adapter.out.rest.question;

import org.flickit.flickitassessmentcore.adapter.out.rest.qualityattribute.QualityAttributeMapper;
import org.flickit.flickitassessmentcore.application.port.out.question.LoadQuestionsByQualityAttributePort;
import org.flickit.flickitassessmentcore.domain.Question;

import java.util.List;
import java.util.stream.Collectors;

public class QuestionMapper {


    public static LoadQuestionsByQualityAttributePort.Result toResult(List<QuestionRestAdapter.QuestionDto> dtos) {
        return new LoadQuestionsByQualityAttributePort.Result(
            dtos.stream().
                map(QuestionMapper::toDomainModel)
                .collect(Collectors.toSet())
        );
    }

    public static Question toDomainModel(QuestionRestAdapter.QuestionDto dto) {
        return new Question(
            dto.id(),
            dto.title(),
            null,
            null,
            null,
            dto.index(),
            null,
            dto.qualityAttributes().stream()
                .map(QualityAttributeMapper::toDomainModel)
                .collect(Collectors.toSet())
        );
    }
}
