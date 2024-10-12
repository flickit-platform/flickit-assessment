package org.flickit.assessment.kit.adapter.out.persistence.subject;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaEntity;
import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaRepository;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaEntity;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaRepository;
import org.flickit.assessment.kit.adapter.out.persistence.attribute.AttributeMapper;
import org.flickit.assessment.kit.application.domain.Subject;
import org.flickit.assessment.kit.application.port.out.subject.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.flickit.assessment.kit.application.port.in.subject.UpdateSubjectsOrderUseCase.SubjectParam;
import org.flickit.assessment.kit.application.port.out.subject.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.flickit.assessment.kit.adapter.out.persistence.subject.SubjectMapper.mapToDomainModel;
import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_KIT_SUBJECT_DETAIL_SUBJECT_ID_NOT_FOUND;
import static org.flickit.assessment.kit.common.ErrorMessageKey.SUBJECT_ID_NOT_FOUND;


@Component
@RequiredArgsConstructor
public class SubjectPersistenceJpaAdapter implements
    UpdateSubjectPort,
    CreateSubjectPort,
    LoadSubjectsPort,
    LoadSubjectPort,
    DeleteSubjectPort,
    UpdateSubjectsIndexPort {

    private final SubjectJpaRepository repository;
    private final AttributeJpaRepository attributeRepository;

    @Override
    public void update(UpdateSubjectPort.Param param) {
        repository.update(param.id(),
            param.kitVersionId(),
            param.title(),
            param.index(),
            param.description(),
            param.lastModificationTime(),
            param.lastModifiedBy()
        );
    }

    @Override
    public Long persist(CreateSubjectPort.Param param) {
        return repository.save(SubjectMapper.mapToJpaEntity(param)).getId();
    }

    @Override
    public List<Subject> loadByKitVersionId(long kitVersionId) {
        List<SubjectJpaEntity> subjectEntities = repository.findAllByKitVersionIdOrderByIndex(kitVersionId);
        List<Long> subjectEntityIds = subjectEntities.stream().map(SubjectJpaEntity::getId).toList();
        List<AttributeJpaEntity> attributeEntities = attributeRepository.findAllBySubjectIdInAndKitVersionId(subjectEntityIds, kitVersionId);
        Map<Long, List<AttributeJpaEntity>> subjectIdToAttrEntities = attributeEntities.stream()
            .collect(Collectors.groupingBy(AttributeJpaEntity::getSubjectId));

        return subjectEntities.stream()
            .map(e -> SubjectMapper.mapToDomainModel(e,
                subjectIdToAttrEntities.get(e.getId()).stream()
                    .map(AttributeMapper::mapToDomainModel)
                    .toList()))
            .toList();
    }

    @Override
    public PaginatedResponse<Subject> loadPaginatedByKitVersionId(long kitVersionId, int page, int size) {
        Page<SubjectJpaEntity> pageResult = repository.findByKitVersionId(kitVersionId,
            PageRequest.of(page, size, Sort.Direction.ASC, SubjectJpaEntity.Fields.index));
        return new PaginatedResponse<>(
            pageResult.stream().map(s -> SubjectMapper.mapToDomainModel(s, null)).toList(),
            pageResult.getNumber(),
            pageResult.getSize(),
            SubjectJpaEntity.Fields.index,
            Sort.Direction.ASC.name().toLowerCase(),
            (int) pageResult.getTotalElements());
    }

    @Override
    public Subject load(long subjectId, long kitVersionId) {
        var subjectEntity = repository.findByIdAndKitVersionId (subjectId, kitVersionId)
            .orElseThrow(() -> new ResourceNotFoundException(GET_KIT_SUBJECT_DETAIL_SUBJECT_ID_NOT_FOUND));
        List<AttributeJpaEntity> attributeEntities = attributeRepository.findAllBySubjectIdAndKitVersionId(subjectId, kitVersionId);
        return mapToDomainModel(subjectEntity,
            attributeEntities.stream().map(AttributeMapper::mapToDomainModel).toList());
    }

    @Override
    public void delete(long subjectId, long kitVersionId) {
        if (!repository.existsByIdAndKitVersionId(subjectId, kitVersionId))
            throw new ResourceNotFoundException(SUBJECT_ID_NOT_FOUND);

        repository.deleteByIdAndKitVersionId(subjectId, kitVersionId);
    }

    @Override
    public void updateIndexes(long kitVersionId, List<SubjectParam> subjects) {
        var ids = subjects.stream().map(SubjectParam::getId).collect(Collectors.toSet());
        var subjectIdToIndexMap = subjects.stream()
            .collect(Collectors.toMap(SubjectParam::getId, SubjectParam::getIndex));
        var entities = repository.findAllByIdInAndKitVersionId(ids, kitVersionId);
        entities.forEach(e -> e.setIndex(subjectIdToIndexMap.get(e.getId())));
        repository.saveAll(entities);
    }
}
