package org.flickit.flickitassessmentcore.adapter.out.persistence.qualityattributevalue;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.adapter.out.persistence.assessmentresult.AssessmentResultJpaEntity;
import org.flickit.flickitassessmentcore.adapter.out.persistence.assessmentresult.AssessmentResultJpaRepository;
import org.flickit.flickitassessmentcore.adapter.out.persistence.attributematurityscore.AttributeMaturityScoreJpaEntity;
import org.flickit.flickitassessmentcore.adapter.out.persistence.attributematurityscore.AttributeMaturityScoreJpaRepository;
import org.flickit.flickitassessmentcore.adapter.out.persistence.attributematurityscore.AttributeMaturityScoreMapper;
import org.flickit.flickitassessmentcore.application.domain.MaturityLevel;
import org.flickit.flickitassessmentcore.application.domain.MaturityScore;
import org.flickit.flickitassessmentcore.application.domain.QualityAttribute;
import org.flickit.flickitassessmentcore.application.domain.QualityAttributeValue;
import org.flickit.flickitassessmentcore.application.port.out.qualityattributevalue.CreateQualityAttributeValuePort;
import org.flickit.flickitassessmentcore.application.port.out.qualityattributevalue.LoadAttributeValueListPort;
import org.flickit.flickitassessmentcore.application.service.exception.ResourceNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.CREATE_QUALITY_ATTRIBUTE_VALUE_ASSESSMENT_RESULT_ID_NOT_FOUND;
import static org.springframework.util.CollectionUtils.isEmpty;

@Component
@RequiredArgsConstructor
public class QualityAttributeValuePersistenceJpaAdapter implements
    CreateQualityAttributeValuePort,
    LoadAttributeValueListPort {

    private final QualityAttributeValueJpaRepository repository;
    private final AssessmentResultJpaRepository assessmentResultRepository;
    private final AttributeMaturityScoreJpaRepository attributeMaturityScoreRepository;

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
        List<QualityAttributeValueJpaEntity> allAttributeValues = repository.findByAssessmentResultId(assessmentResultId);
        Map<UUID, List<AttributeMaturityScoreJpaEntity>> attributeIdToScores =
            attributeMaturityScoreRepository.findByAttributeValueIdIn(collectIds(allAttributeValues)).stream()
                .collect(groupingBy(AttributeMaturityScoreJpaEntity::getAttributeValueId));

        return allAttributeValues.stream()
            .map(x -> new QualityAttributeValue(
                x.getId(),
                new QualityAttribute(x.getQualityAttributeId(), 1, null),
                null,
                toMaturityScore(attributeIdToScores, x),
                maturityLevels.get(x.getMaturityLevelId())
            ))
            .toList();
    }

    private static Set<UUID> collectIds(List<QualityAttributeValueJpaEntity> allAttributeValues) {
        return allAttributeValues.stream().map(QualityAttributeValueJpaEntity::getId).collect(Collectors.toSet());
    }

    private static Set<MaturityScore> toMaturityScore(Map<UUID, List<AttributeMaturityScoreJpaEntity>> attributeIdToScores,
                                                      QualityAttributeValueJpaEntity x) {
        List<AttributeMaturityScoreJpaEntity> scores = attributeIdToScores.get(x.getId());
        if (isEmpty(scores))
            return Set.of();
        return scores.stream()
            .map(AttributeMaturityScoreMapper::mapToDomain)
            .collect(Collectors.toSet());
    }
}
