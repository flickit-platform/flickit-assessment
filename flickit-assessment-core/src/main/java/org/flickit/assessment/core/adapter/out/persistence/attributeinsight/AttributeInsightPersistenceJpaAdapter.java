package org.flickit.assessment.core.adapter.out.persistence.attributeinsight;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.domain.AttributeInsight;
import org.flickit.assessment.core.application.port.out.attributeinsight.LoadAttributeInsightPort;
import org.flickit.assessment.data.jpa.core.attributeinsight.AttributeInsightJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AttributeInsightPersistenceJpaAdapter implements LoadAttributeInsightPort {

    private final AttributeInsightJpaRepository repository;

    @Override
    public Optional<AttributeInsight> loadAttributeAiInsight(UUID assessmentResultId, Long attributeId) {
        return repository.findByAssessmentResultIdAndAttributeId (assessmentResultId, attributeId)
            .map(AttributeInsightMapper::mapToDomain);
    }
}
