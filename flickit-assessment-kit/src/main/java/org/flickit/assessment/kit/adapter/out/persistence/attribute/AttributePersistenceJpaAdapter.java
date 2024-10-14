package org.flickit.assessment.kit.adapter.out.persistence.attribute;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaEntity.Fields;
import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaRepository;
import org.flickit.assessment.data.jpa.kit.attribute.AttributeWithSubjectView;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaEntity;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaRepository;
import org.flickit.assessment.kit.adapter.out.persistence.subject.SubjectMapper;
import org.flickit.assessment.kit.application.domain.Attribute;
import org.flickit.assessment.kit.application.domain.Subject;
import org.flickit.assessment.kit.application.port.out.attribute.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.flickit.assessment.kit.adapter.out.persistence.attribute.AttributeMapper.mapToDomainModel;
import static org.flickit.assessment.kit.adapter.out.persistence.attribute.AttributeMapper.mapToJpaEntity;
import static org.flickit.assessment.kit.common.ErrorMessageKey.CREATE_ATTRIBUTE_SUBJECT_ID_NOT_FOUND;
import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_KIT_ATTRIBUTE_DETAIL_ATTRIBUTE_ID_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class AttributePersistenceJpaAdapter implements
    UpdateAttributePort,
    CreateAttributePort,
    LoadAttributePort,
    CountAttributeImpactfulQuestionsPort,
    LoadAllAttributesPort,
    LoadAttributesPort {

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
    public PaginatedResponse<Subject> loadByKitVersionId(long kitVersionId, int size, int page) {
        var pageResult = repository.findAllByKitVersionId(kitVersionId, PageRequest.of(page, size));
        var subjectToAttributesMap = pageResult.getContent().stream()
            .collect(groupingBy(AttributeWithSubjectView::getSubject, LinkedHashMap::new, toList()));

        var items = subjectToAttributesMap.entrySet().stream()
            .map(x -> {
                List<Attribute> attributes = x.getValue().stream()
                    .map(a -> mapToDomainModel(a.getAttribute()))
                    .toList();
                return SubjectMapper.mapToDomainModel(x.getKey(), attributes);
            })
            .toList();

        return new PaginatedResponse<>(items,
            pageResult.getNumber(),
            pageResult.getSize(),
            Fields.index.toLowerCase(),
            Sort.Direction.ASC.name().toLowerCase(),
            (int) pageResult.getTotalElements());
    }
}
