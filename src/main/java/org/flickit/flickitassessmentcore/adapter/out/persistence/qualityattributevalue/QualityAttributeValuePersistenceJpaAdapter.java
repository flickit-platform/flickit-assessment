package org.flickit.flickitassessmentcore.adapter.out.persistence.qualityattributevalue;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.adapter.out.persistence.assessmentresult.AssessmentResultJpaEntity;
import org.flickit.flickitassessmentcore.adapter.out.persistence.assessmentresult.AssessmentResultJpaRepository;
import org.flickit.flickitassessmentcore.adapter.out.persistence.attributematurityscore.AttributeMaturityScoreMapper;
import org.flickit.flickitassessmentcore.application.domain.MaturityLevel;
import org.flickit.flickitassessmentcore.application.domain.QualityAttribute;
import org.flickit.flickitassessmentcore.application.domain.QualityAttributeValue;
import org.flickit.flickitassessmentcore.application.port.out.qualityattributevalue.CreateQualityAttributeValuePort;
import org.flickit.flickitassessmentcore.application.port.out.qualityattributevalue.LoadAttributeValueListPort;
import org.flickit.flickitassessmentcore.application.service.exception.ResourceNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.CREATE_QUALITY_ATTRIBUTE_VALUE_ASSESSMENT_RESULT_ID_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class QualityAttributeValuePersistenceJpaAdapter implements
    CreateQualityAttributeValuePort,
    LoadAttributeValueListPort {

    private final QualityAttributeValueJpaRepository repository;
    private final AssessmentResultJpaRepository assessmentResultRepository;

    @Override
    public void persistAll(List<Long> qualityAttributeIds, UUID assessmentResultId) {
        AssessmentResultJpaEntity assessmentResult = assessmentResultRepository.findById(assessmentResultId)
            .orElseThrow(() -> new ResourceNotFoundException(CREATE_QUALITY_ATTRIBUTE_VALUE_ASSESSMENT_RESULT_ID_NOT_FOUND));

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
                x.getMaturityScores().stream()
                    .map(AttributeMaturityScoreMapper::mapToDomain)
                    .collect(Collectors.toSet()),
                maturityLevels.get(x.getMaturityLevelId())
            ))
            .toList();
    }
}
