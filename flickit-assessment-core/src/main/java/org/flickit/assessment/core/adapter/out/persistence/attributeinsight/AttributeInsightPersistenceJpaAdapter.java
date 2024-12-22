package org.flickit.assessment.core.adapter.out.persistence.attributeinsight;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.domain.AttributeInsight;
import org.flickit.assessment.core.application.domain.assessmentdashboard.Insights;
import org.flickit.assessment.core.application.port.out.attributeinsight.CreateAttributeInsightPort;
import org.flickit.assessment.core.application.port.out.attributeinsight.LoadAttributeInsightPort;
import org.flickit.assessment.core.application.port.out.attributeinsight.LoadInsightsDashboardPort;
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
    UpdateAttributeInsightPort,
    LoadInsightsDashboardPort {

    private final AttributeInsightJpaRepository repository;

    @Override
    public Optional<AttributeInsight> loadAttributeAiInsight(UUID assessmentResultId, Long attributeId) {
        return repository.findByAssessmentResultIdAndAttributeId(assessmentResultId, attributeId)
            .map(AttributeInsightMapper::mapToDomain);
    }

    @Override
    public void persist(AttributeInsight attributeInsight) {
        repository.save(AttributeInsightMapper.mapToJpaEntity(attributeInsight));
    }

    @Override
    public void updateAiInsight(AttributeInsight attributeInsight) {
        repository.updateAiInsight(
            attributeInsight.getAssessmentResultId(),
            attributeInsight.getAttributeId(),
            attributeInsight.getAiInsight(),
            attributeInsight.getAiInsightTime(),
            attributeInsight.getAiInputPath());
    }

    @Override
    public void updateAssessorInsight(AttributeInsight attributeInsight) {
        repository.updateAssessorInsight(
            attributeInsight.getAssessmentResultId(),
            attributeInsight.getAttributeId(),
            attributeInsight.getAssessorInsight(),
            attributeInsight.getAssessorInsightTime()
        );
    }

    @Override
    public Insights loadInsights(long kitVersionId) {
        return null;
    }
}
