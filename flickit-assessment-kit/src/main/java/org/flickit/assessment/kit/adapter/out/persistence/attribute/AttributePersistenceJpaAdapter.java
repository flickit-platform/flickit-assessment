package org.flickit.assessment.kit.adapter.out.persistence.attribute;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaEntity;
import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaRepository;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaEntity;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaRepository;
import org.flickit.assessment.kit.application.domain.Attribute;
import org.flickit.assessment.kit.application.port.out.attribute.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.flickit.assessment.kit.adapter.out.persistence.attribute.AttributeMapper.mapToDomainModel;
import static org.flickit.assessment.kit.adapter.out.persistence.attribute.AttributeMapper.mapToJpaEntity;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;

@Component
@RequiredArgsConstructor
public class AttributePersistenceJpaAdapter implements
    UpdateAttributePort,
    CreateAttributePort,
    LoadAttributePort,
    CountAttributeImpactfulQuestionsPort,
    LoadAllAttributesPort,
    DeleteAttributePort {

    private final AttributeJpaRepository repository;
    private final SubjectJpaRepository subjectRepository;

    @Override
    public void update(UpdateAttributePort.Param param) {
        repository.update(param.id(),
            param.kitVersionId(),
            param.title(),
            param.index(),
            param.description(),
            param.weight(),
            param.lastModificationTime(),
            param.lastModifiedBy(),
            param.subjectId());
    }

    @Override
    public void updateOrders(UpdateOrderParam param) {
        Map<AttributeJpaEntity.EntityId, Integer> idToIndex = param.orders().stream()
            .collect(Collectors.toMap(
                ml -> new AttributeJpaEntity.EntityId(ml.attributeId(), param.kitVersionId()),
                UpdateOrderParam.AttributeOrder::index
            ));
        List<AttributeJpaEntity> entities = repository.findAllById(idToIndex.keySet());
        if (entities.size() != param.orders().size())
            throw new ResourceNotFoundException(ATTRIBUTE_ID_NOT_FOUND);

        entities.forEach(x -> {
            int newIndex = idToIndex.get(new AttributeJpaEntity.EntityId(x.getId(), param.kitVersionId()));
            x.setIndex(newIndex);
            x.setLastModificationTime(param.lastModificationTime());
            x.setLastModifiedBy(param.lastModifiedBy());
        });
        repository.saveAll(entities);
    }

    @Override
    public Long persist(Attribute attribute, Long subjectId, Long kitVersionId) {
        var entityId = new SubjectJpaEntity.EntityId(subjectId, kitVersionId);
        SubjectJpaEntity subjectJpaEntity = subjectRepository.findById(entityId)
            .orElseThrow(() -> new ResourceNotFoundException(CREATE_ATTRIBUTE_SUBJECT_ID_NOT_FOUND));
        return repository.save(mapToJpaEntity(attribute, subjectJpaEntity)).getId();
    }

    @Override
    public Attribute load(Long attributeId, Long kitVersionId) {
        var attribute = repository.findByIdAndKitVersionId(attributeId, kitVersionId)
            .orElseThrow(() -> new ResourceNotFoundException(GET_KIT_ATTRIBUTE_DETAIL_ATTRIBUTE_ID_NOT_FOUND));
        return mapToDomainModel(attribute);
    }

    @Override
    public int countQuestions(long attributeId, long kitVersionId) {
        return repository.countAttributeImpactfulQuestions(attributeId, kitVersionId);
    }

    @Override
    public List<Attribute> loadAllByIdsAndKitVersionId(List<Long> attributeIds, long kitVersionId) {
        return repository.findAllByIdInAndKitVersionId(attributeIds, kitVersionId).stream()
            .map(AttributeMapper::mapToDomainModel)
            .toList();
    }

    @Override
    public void delete(long attributeId, long kitVersionId) {
        if (!repository.existsByIdAndKitVersionId(attributeId, kitVersionId))
            throw new ResourceNotFoundException(ATTRIBUTE_ID_NOT_FOUND);

        repository.deleteByIdAndKitVersionId(attributeId, kitVersionId);
    }
}
