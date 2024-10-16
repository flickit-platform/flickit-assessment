package org.flickit.assessment.kit.adapter.out.persistence.attribute;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaEntity;
import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaEntity.Fields;
import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaRepository;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaEntity;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaRepository;
import org.flickit.assessment.kit.application.domain.Attribute;
import org.flickit.assessment.kit.application.port.in.attribute.GetAttributesUseCase.AttributeListItem;
import org.flickit.assessment.kit.application.port.in.attribute.GetAttributesUseCase.AttributeSubject;
import org.flickit.assessment.kit.application.port.out.attribute.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.flickit.assessment.kit.adapter.out.persistence.attribute.AttributeMapper.*;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;

@Component
@RequiredArgsConstructor
public class AttributePersistenceJpaAdapter implements
    UpdateAttributePort,
    CreateAttributePort,
    LoadAttributePort,
    CountAttributeImpactfulQuestionsPort,
    LoadAllAttributesPort,
    DeleteAttributePort,
    LoadAttributesPort {

    private final AttributeJpaRepository repository;
    private final SubjectJpaRepository subjectRepository;

    @Override
    public void update(UpdateAttributePort.Param param) {
        if (!repository.existsByIdAndKitVersionId(param.id(), param.kitVersionId()))
            throw new ResourceNotFoundException(ATTRIBUTE_ID_NOT_FOUND);

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

    @Override
    public PaginatedResponse<AttributeListItem> loadByKitVersionId(long kitVersionId, int size, int page) {
        var pageResult = repository.findAllByKitVersionId(kitVersionId, PageRequest.of(page, size));

        var subjectIds = pageResult.getContent().stream()
            .map(AttributeJpaEntity::getSubjectId)
            .collect(Collectors.toSet());

        var subjectIdToAttributeSubjectMap = subjectRepository.findAllByIdInAndKitVersionId(subjectIds, kitVersionId).stream()
            .map(x -> new AttributeSubject(x.getId(), x.getTitle()))
            .collect(Collectors.toMap(AttributeSubject::id, Function.identity()));

        var items = pageResult.getContent().stream()
            .map(x -> mapToListItem(x, subjectIdToAttributeSubjectMap.get(x.getSubjectId())))
            .toList();

        return new PaginatedResponse<>(items,
            pageResult.getNumber(),
            pageResult.getSize(),
            Fields.index,
            Sort.Direction.ASC.name().toLowerCase(),
            (int) pageResult.getTotalElements());
    }
}
