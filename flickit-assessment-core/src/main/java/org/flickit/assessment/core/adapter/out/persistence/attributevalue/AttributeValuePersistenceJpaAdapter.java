package org.flickit.assessment.core.adapter.out.persistence.attributevalue;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.adapter.out.persistence.attributematurityscore.AttributeMaturityScoreMapper;
import org.flickit.assessment.core.application.domain.MaturityLevel;
import org.flickit.assessment.core.application.domain.MaturityScore;
import org.flickit.assessment.core.application.domain.Attribute;
import org.flickit.assessment.core.application.domain.AttributeValue;
import org.flickit.assessment.core.application.port.out.attributevalue.CreateAttributeValuePort;
import org.flickit.assessment.core.application.port.out.attributevalue.LoadAttributeValueListPort;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaEntity;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaRepository;
import org.flickit.assessment.data.jpa.core.attributematurityscore.AttributeMaturityScoreJpaEntity;
import org.flickit.assessment.data.jpa.core.attributematurityscore.AttributeMaturityScoreJpaRepository;
import org.flickit.assessment.data.jpa.core.attributevalue.AttributeValueJpaEntity;
import org.flickit.assessment.data.jpa.core.attributevalue.AttributeValueJpaRepository;
import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaEntity;
import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;
import static org.flickit.assessment.core.common.ErrorMessageKey.CREATE_ATTRIBUTE_VALUE_ASSESSMENT_RESULT_ID_NOT_FOUND;
import static org.springframework.util.CollectionUtils.isEmpty;

@Component
@RequiredArgsConstructor
public class AttributeValuePersistenceJpaAdapter implements
    CreateAttributeValuePort,
    LoadAttributeValueListPort {

    private final AttributeValueJpaRepository repository;
    private final AssessmentResultJpaRepository assessmentResultRepository;
    private final AttributeMaturityScoreJpaRepository attributeMaturityScoreRepository;
    private final AttributeJpaRepository attributeRepository;

    @Override
    public List<AttributeValue> persistAll(List<Long> attributeIds, UUID assessmentResultId) {
        AssessmentResultJpaEntity assessmentResult = assessmentResultRepository.findById(assessmentResultId)
            .orElseThrow(() -> new ResourceNotFoundException(CREATE_ATTRIBUTE_VALUE_ASSESSMENT_RESULT_ID_NOT_FOUND));

        List<AttributeJpaEntity> attributeEntities = attributeRepository.findAllByIdInAndKitVersionId(attributeIds, assessmentResult.getKitVersionId());
        Map<Long, AttributeJpaEntity> attrEntityIdToAttrEntity = attributeEntities.stream()
            .collect(toMap(AttributeJpaEntity::getId, Function.identity())); //todo: should be deleted, when deleting refNum column

        List<AttributeValueJpaEntity> entities = attributeIds.stream().map(attributeId -> {
            AttributeJpaEntity attributeEntity = attrEntityIdToAttrEntity.get(attributeId);
            AttributeValueJpaEntity attributeValue = AttributeValueMapper.mapToJpaEntity(attributeId, attributeEntity.getRefNum());
            attributeValue.setAssessmentResult(assessmentResult);
            return attributeValue;
        }).toList();

        var persistedEntities = repository.saveAll(entities);

        return persistedEntities.stream().map(q -> {
            AttributeJpaEntity attributeEntity = attrEntityIdToAttrEntity.get(q.getAttributeId());
            return AttributeValueMapper.mapToDomainModel(q, attributeEntity);
        }).toList();
    }

    @Override
    public List<AttributeValue> loadAll(UUID assessmentResultId, Map<Long, MaturityLevel> maturityLevels) {
        List<AttributeValueJpaEntity> entities = repository.findByAssessmentResultId(assessmentResultId);

        return toAttributeValues(entities, maturityLevels);
    }

    private List<AttributeValue> toAttributeValues(List<AttributeValueJpaEntity> entities, Map<Long, MaturityLevel> maturityLevels) {
        Map<UUID, List<AttributeMaturityScoreJpaEntity>> attributeIdToScores =
            attributeMaturityScoreRepository.findByAttributeValueIdIn(collectIds(entities)).stream()
                .collect(groupingBy(AttributeMaturityScoreJpaEntity::getAttributeValueId));

        List<UUID> attributeRefNums = entities.stream().map(AttributeValueJpaEntity::getAttributeRefNum).toList();
        Long kitVersionId = entities.get(0).getAssessmentResult().getKitVersionId();
        Map<UUID, Long> attributeIdsToRefNumMap = attributeRepository.findAllByKitVersionIdAndRefNumIn(kitVersionId, attributeRefNums).stream()
            .collect(toMap(AttributeJpaEntity::getRefNum, AttributeJpaEntity::getId));

        return entities.stream()
            .map(x -> new AttributeValue(
                x.getId(),
                new Attribute(attributeIdsToRefNumMap.get(x.getAttributeRefNum()), 1, null),
                null,
                toMaturityScore(attributeIdToScores, x),
                maturityLevels.get(x.getMaturityLevelId()),
                x.getConfidenceValue()
            ))
            .toList();
    }

    private static Set<UUID> collectIds(List<AttributeValueJpaEntity> allAttributeValues) {
        return allAttributeValues.stream().map(AttributeValueJpaEntity::getId).collect(Collectors.toSet());
    }

    private static Set<MaturityScore> toMaturityScore(Map<UUID, List<AttributeMaturityScoreJpaEntity>> attributeIdToScores,
                                                      AttributeValueJpaEntity x) {
        List<AttributeMaturityScoreJpaEntity> scores = attributeIdToScores.get(x.getId());
        if (isEmpty(scores))
            return Set.of();
        return scores.stream()
            .map(AttributeMaturityScoreMapper::mapToDomain)
            .collect(Collectors.toSet());
    }
}
