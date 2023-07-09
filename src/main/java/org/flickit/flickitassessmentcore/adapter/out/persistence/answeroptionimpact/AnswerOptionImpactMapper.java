package org.flickit.flickitassessmentcore.adapter.out.persistence.answeroptionimpact;

import org.flickit.flickitassessmentcore.application.port.out.answeroptionimpact.LoadAnswerOptionImpactsByAnswerOptionPort;
import org.flickit.flickitassessmentcore.domain.AnswerOptionImpact;

import java.util.List;
import java.util.stream.Collectors;

public class AnswerOptionImpactMapper {
    public static LoadAnswerOptionImpactsByAnswerOptionPort.Result toResult(List<AnswerOptionImpactPersistenceJpaAdapter.AnswerOptionImpactDto> dtos) {
        return new LoadAnswerOptionImpactsByAnswerOptionPort.Result(
            dtos.stream().
                map(AnswerOptionImpactMapper::toDomainModel).
                collect(Collectors.toSet())
        );
    }

    private static AnswerOptionImpact toDomainModel(AnswerOptionImpactPersistenceJpaAdapter.AnswerOptionImpactDto dto) {
        return new AnswerOptionImpact(
            dto.id(),
            dto.value(),
            dto.option_id(),
            dto.metric_impact_id()
        );
    }
}
