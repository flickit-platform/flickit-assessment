package org.flickit.assessment.core.adapter.out.persistence.qualityattributevalue;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.adapter.out.persistence.attributematurityscore.AttributeMaturityScoreMapper;
import org.flickit.assessment.core.application.domain.MaturityLevel;
import org.flickit.assessment.core.application.domain.MaturityScore;
import org.flickit.assessment.core.application.domain.QualityAttribute;
import org.flickit.assessment.core.application.domain.QualityAttributeValue;
import org.flickit.assessment.core.application.port.out.qualityattributevalue.CreateQualityAttributeValuePort;
import org.flickit.assessment.core.application.port.out.qualityattributevalue.LoadAttributeValueListPort;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaEntity;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaRepository;
import org.flickit.assessment.data.jpa.core.attributematurityscore.AttributeMaturityScoreJpaEntity;
import org.flickit.assessment.data.jpa.core.attributematurityscore.AttributeMaturityScoreJpaRepository;
import org.flickit.assessment.data.jpa.core.attributevalue.QualityAttributeValueJpaEntity;
import org.flickit.assessment.data.jpa.core.attributevalue.QualityAttributeValueJpaRepository;
import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaEntity;
import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;
import static org.flickit.assessment.core.common.ErrorMessageKey.CREATE_QUALITY_ATTRIBUTE_VALUE_ASSESSMENT_RESULT_ID_NOT_FOUND;
import static org.springframework.util.CollectionUtils.isEmpty;

@Component
@RequiredArgsConstructor
public class QualityAttributeValuePersistenceJpaAdapter implements
    CreateQualityAttributeValuePort,
    LoadAttributeValueListPort {

    private final QualityAttributeValueJpaRepository repository;
    private final AssessmentResultJpaRepository assessmentResultRepository;
    private final AttributeMaturityScoreJpaRepository attributeMaturityScoreRepository;
    private final AttributeJpaRepository attributeRepository;

    @Override
    public List<QualityAttributeValue> persistAll(List<Long> qualityAttributeIds, UUID assessmentResultId) {
        AssessmentResultJpaEntity assessmentResult = assessmentResultRepository.findById(assessmentResultId)
            .orElseThrow(() -> new ResourceNotFoundException(CREATE_QUALITY_ATTRIBUTE_VALUE_ASSESSMENT_RESULT_ID_NOT_FOUND));

        List<QualityAttributeValueJpaEntity> entities = qualityAttributeIds.stream().map(qualityAttributeId -> {
            UUID attributeRefNum = attributeRepository.findRefNumById(qualityAttributeId);
            QualityAttributeValueJpaEntity qualityAttributeValue = QualityAttributeValueMapper.mapToJpaEntity(attributeRefNum);
            qualityAttributeValue.setAssessmentResult(assessmentResult);
            return qualityAttributeValue;
        }).toList();

        var persistedEntities = repository.saveAll(entities);

        return persistedEntities.stream().map(q -> {
            var attributeEntity = attributeRepository.findByKitVersionIdAndRefNum(assessmentResult.getKitVersionId(), q.getAttributeRefNum());
            return QualityAttributeValueMapper.mapToDomainModel(q, attributeEntity);
        }).toList();
    }

    @Override
    public List<QualityAttributeValue> loadAll(UUID assessmentResultId, Map<Long, MaturityLevel> maturityLevels) {
        List<QualityAttributeValueJpaEntity> entities = repository.findByAssessmentResultId(assessmentResultId);

        return toAttributeValues(entities, maturityLevels);
    }

    public List<QualityAttributeValue> loadBySubjectId(UUID assessmentResultId, Long subjectId, Map<Long, MaturityLevel> maturityLevels) {
        List<QualityAttributeValueJpaEntity> entities = repository.findByAssessmentResultIdAndSubjectId(assessmentResultId, subjectId);

        return toAttributeValues(entities, maturityLevels);
    }

    private List<QualityAttributeValue> toAttributeValues(List<QualityAttributeValueJpaEntity> entities, Map<Long, MaturityLevel> maturityLevels) {
        Map<UUID, List<AttributeMaturityScoreJpaEntity>> attributeIdToScores =
            attributeMaturityScoreRepository.findByAttributeValueIdIn(collectIds(entities)).stream()
                .collect(groupingBy(AttributeMaturityScoreJpaEntity::getAttributeValueId));

        List<UUID> attributeRefNums = entities.stream().map(QualityAttributeValueJpaEntity::getAttributeRefNum).toList();
        Long kitVersionId = entities.get(0).getAssessmentResult().getKitVersionId();
        Map<UUID, Long> attributeIdsToRefNumMap = attributeRepository.findAllByKitVersionIdAndRefNumIn(kitVersionId, attributeRefNums).stream()
            .collect(toMap(AttributeJpaEntity::getRefNum, AttributeJpaEntity::getId));

        return entities.stream()
            .map(x -> new QualityAttributeValue(
                x.getId(),
                new QualityAttribute(attributeIdsToRefNumMap.get(x.getAttributeRefNum()), 1, null),
                null,
                toMaturityScore(attributeIdToScores, x),
                maturityLevels.get(x.getMaturityLevelId()),
                x.getConfidenceValue()
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
