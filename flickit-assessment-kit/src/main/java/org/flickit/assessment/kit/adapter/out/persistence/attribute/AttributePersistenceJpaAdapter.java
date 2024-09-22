package org.flickit.assessment.kit.adapter.out.persistence.attribute;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaRepository;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaEntity;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaRepository;
import org.flickit.assessment.kit.application.domain.Attribute;
import org.flickit.assessment.kit.application.port.out.attribute.*;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.flickit.assessment.kit.adapter.out.persistence.attribute.AttributeMapper.mapToDomainModel;
import static org.flickit.assessment.kit.adapter.out.persistence.attribute.AttributeMapper.mapToJpaEntity;
import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_KIT_ATTRIBUTE_DETAIL_ATTRIBUTE_ID_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class AttributePersistenceJpaAdapter implements
    UpdateAttributePort,
    CreateAttributePort,
    LoadAttributePort,
    CountAttributeImpactfulQuestionsPort,
    LoadAllAttributesPort {

    private final AttributeJpaRepository repository;
    private final SubjectJpaRepository subjectRepository;

    @Override
    public void update(UpdateAttributePort.Param param) {
        repository.update(param.id(),
            param.kitVersionId(),
            param.code(),
            param.title(),
            param.index(),
            param.description(),
            param.weight(),
            param.lastModificationTime(),
            param.lastModifiedBy(),
            param.subjectId());
    }

    @Override
    public Long persist(Attribute attribute, Long subjectId, Long kitVersionId) {
        var entityId = new SubjectJpaEntity.EntityId(subjectId, kitVersionId);
        SubjectJpaEntity subjectJpaEntity = subjectRepository.getReferenceById(entityId);
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
}
