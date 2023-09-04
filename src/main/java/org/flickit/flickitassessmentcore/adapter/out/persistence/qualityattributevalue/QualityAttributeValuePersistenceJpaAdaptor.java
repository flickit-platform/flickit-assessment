package org.flickit.flickitassessmentcore.adapter.out.persistence.qualityattributevalue;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.adapter.out.persistence.assessmentresult.AssessmentResultJpaEntity;
import org.flickit.flickitassessmentcore.adapter.out.persistence.assessmentresult.AssessmentResultJpaRepository;
import org.flickit.flickitassessmentcore.application.domain.MaturityLevel;
import org.flickit.flickitassessmentcore.application.domain.QualityAttribute;
import org.flickit.flickitassessmentcore.application.domain.QualityAttributeValue;
import org.flickit.flickitassessmentcore.application.port.out.qualityattributevalue.CreateQualityAttributeValuePort;
import org.flickit.flickitassessmentcore.application.port.out.qualityattributevalue.LoadAttributeValueListPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class QualityAttributeValuePersistenceJpaAdaptor implements
    CreateQualityAttributeValuePort,
    LoadAttributeValueListPort {

    private final QualityAttributeValueJpaRepository repository;
    private final AssessmentResultJpaRepository assessmentResultRepository;

    @Override
    public void persistAll(List<Long> qualityAttributeIds, UUID assessmentResultId) {
        AssessmentResultJpaEntity assessmentResult = assessmentResultRepository.findById(assessmentResultId).get();

        List<QualityAttributeValueJpaEntity> entities = qualityAttributeIds.stream().map(qualityAttributeId -> {
            QualityAttributeValueJpaEntity qualityAttributeValue = QualityAttributeValueMapper.mapToJpaEntity(qualityAttributeId);
            qualityAttributeValue.setAssessmentResult(assessmentResult);
            return qualityAttributeValue;
        }).toList();

        repository.saveAll(entities);
    }

    @Override
    public List<QualityAttributeValue> loadAttributeValues(UUID assessmentResultId, Map<Long, MaturityLevel> maturityLevels) {
        List<QualityAttributeValueJpaEntity> allAttributeValueEntities = repository.findByAssessmentResultId(assessmentResultId);
        return allAttributeValueEntities.stream()
            .map(x -> new QualityAttributeValue(
                x.getId(),
                new QualityAttribute(x.getQualityAttributeId(), 1, null),
                null,
                maturityLevels.get(x.getMaturityLevelId())
            ))
            .toList();
    }
}
