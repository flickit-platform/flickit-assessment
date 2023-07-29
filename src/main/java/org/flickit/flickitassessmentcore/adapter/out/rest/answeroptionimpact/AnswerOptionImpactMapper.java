package org.flickit.flickitassessmentcore.adapter.out.rest.answeroptionimpact;

import org.flickit.flickitassessmentcore.application.port.out.answeroptionimpact.LoadAnswerOptionImpactsByAnswerOptionAndQualityAttributePort;
import org.flickit.flickitassessmentcore.domain.AnswerOptionImpact;

import java.util.List;
import java.util.stream.Collectors;

public class AnswerOptionImpactMapper {
    public static LoadAnswerOptionImpactsByAnswerOptionAndQualityAttributePort.Result toResult(List<AnswerOptionImpactRestAdapterAndQualityAttribute.AnswerOptionImpactDto> dtos) {
        return new LoadAnswerOptionImpactsByAnswerOptionAndQualityAttributePort.Result(
            dtos.stream().
                map(AnswerOptionImpactMapper::toDomainModel).
                collect(Collectors.toList())
        );
    }

    static AnswerOptionImpact toDomainModel(AnswerOptionImpactRestAdapterAndQualityAttribute.AnswerOptionImpactDto dto) {
        return new AnswerOptionImpact(
            dto.id(),
            dto.value(),
            dto.option_id(),
            dto.metric_impact_id()
        );
    }
}
