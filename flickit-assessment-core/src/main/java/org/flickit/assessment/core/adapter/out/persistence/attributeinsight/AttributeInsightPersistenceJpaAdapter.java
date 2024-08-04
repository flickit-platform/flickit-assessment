package org.flickit.assessment.core.adapter.out.persistence.attributeinsight;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.OpenAiProperties;
import org.flickit.assessment.core.application.domain.AttributeInsight;
import org.flickit.assessment.core.application.port.out.attributeinsight.CreateAttributeInsightPort;
import org.flickit.assessment.core.application.port.out.attributeinsight.LoadAttributeInsightPort;
import org.flickit.assessment.core.application.port.out.attributeinsight.UpdateAttributeInsightPort;
import org.flickit.assessment.data.jpa.core.attributeinsight.AttributeInsightJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AttributeInsightPersistenceJpaAdapter implements
    LoadAttributeInsightPort,
    CreateAttributeInsightPort,
    UpdateAttributeInsightPort {

    private final OpenAiProperties openAiProperties;
    private final AttributeInsightJpaRepository repository;

    @Override
    public Optional<AttributeInsight> loadAttributeAiInsight(UUID assessmentResultId, Long attributeId) {
        return repository.findByAssessmentResultIdAndAttributeId(assessmentResultId, attributeId)
            .map(AttributeInsightMapper::mapToDomain);
    }

    @Override
    public void persist(CreateAttributeInsightPort.Param param) {
        repository.save(AttributeInsightMapper.mapCreateParamToJpaEntity(param));
    }

    @Override
    public void update(UpdateAttributeInsightPort.Param param) {
        repository.update(param.assessmentResultId(),
            param.attributeId(),
            param.aiInsight(),
            param.assessorInsight(),
            param.aiInsightTime(),
            param.assessorInsightTime(),
            param.aiInputPath());
    }
}
