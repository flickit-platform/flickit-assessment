package org.flickit.assessment.core.adapter.out.persistence.attributevalue;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AttributeValue;
import org.flickit.assessment.core.application.port.out.attributevalue.CreateAttributeValuePort;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaEntity;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaRepository;
import org.flickit.assessment.data.jpa.core.attributevalue.AttributeValueJpaEntity;
import org.flickit.assessment.data.jpa.core.attributevalue.AttributeValueJpaRepository;
import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaEntity;
import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;
import static org.flickit.assessment.core.common.ErrorMessageKey.CREATE_ATTRIBUTE_VALUE_ASSESSMENT_RESULT_ID_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class AttributeValuePersistenceJpaAdapter implements
    CreateAttributeValuePort {

    private final AttributeValueJpaRepository repository;
    private final AssessmentResultJpaRepository assessmentResultRepository;
    private final AttributeJpaRepository attributeRepository;

    @Override
    public List<AttributeValue> persistAll(List<Long> attributeIds, UUID assessmentResultId) {
        AssessmentResultJpaEntity assessmentResult = assessmentResultRepository.findById(assessmentResultId)
            .orElseThrow(() -> new ResourceNotFoundException(CREATE_ATTRIBUTE_VALUE_ASSESSMENT_RESULT_ID_NOT_FOUND));

        List<AttributeJpaEntity> attributeEntities = attributeRepository.findAllByIdInAndKitVersionId(attributeIds, assessmentResult.getKitVersionId());
        Map<Long, AttributeJpaEntity> attrIdToAttrEntity = attributeEntities.stream()
            .collect(toMap(AttributeJpaEntity::getId, Function.identity()));

        List<AttributeValueJpaEntity> entities = attributeIds.stream().map(attributeId -> {
            AttributeValueJpaEntity attributeValue = AttributeValueMapper.mapToJpaEntity(attributeId);
            attributeValue.setAssessmentResult(assessmentResult);
            return attributeValue;
        }).toList();

        var persistedEntities = repository.saveAll(entities);

        return persistedEntities.stream().map(q -> {
            AttributeJpaEntity attributeEntity = attrIdToAttrEntity.get(q.getAttributeId());
            return AttributeValueMapper.mapToDomainModel(q, attributeEntity);
        }).toList();
    }
}
