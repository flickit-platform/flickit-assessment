package org.flickit.assessment.kit.adapter.out.persistence.subject;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.data.jpa.kit.assessmentkit.AssessmentKitJpaRepository;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaEntity;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaRepository;
import org.flickit.assessment.kit.adapter.out.persistence.attribute.AttributeMapper;
import org.flickit.assessment.kit.application.domain.Subject;
import org.flickit.assessment.kit.application.port.out.subject.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static org.flickit.assessment.kit.adapter.out.persistence.subject.SubjectMapper.mapToDomainModel;
import static org.flickit.assessment.kit.common.ErrorMessageKey.KIT_ID_NOT_FOUND;


@Component
@RequiredArgsConstructor
public class SubjectPersistenceJpaAdapter implements
    UpdateSubjectPort,
    CreateSubjectPort,
    LoadSubjectsPort,
    LoadSubjectDetailPort,
    CheckSubjectExistencePort {

    private final SubjectJpaRepository repository;
    private final AssessmentKitJpaRepository assessmentKitRepository;

    @Override
    public void update(UpdateSubjectPort.Param param) {
        repository.update(param.id(),
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
    public List<Subject> loadByKitId(long kitId) {
        var kitVersionId = assessmentKitRepository.findById(kitId)
            .orElseThrow(() -> new ResourceNotFoundException(KIT_ID_NOT_FOUND))
            .getKitVersionId();

        return repository.findAllByKitVersionIdOrderByIndex(kitVersionId).stream()
            .map(e -> SubjectMapper.mapToDomainModel(e, null))
            .toList();
    }

    @Override
    public boolean exist(long kitId, long subjectId) {
        Long kitVersionId = assessmentKitRepository.findById(kitId)
            .orElseThrow(() -> new ResourceNotFoundException(KIT_ID_NOT_FOUND))
            .getKitVersionId();

        return repository.findByIdAndKitVersionId(subjectId, kitVersionId).isPresent();
    }

    @Override
    public Optional<Subject> loadById(long subjectId) {
        return repository.findById(subjectId)
            .map((SubjectJpaEntity entity) -> mapToDomainModel(entity,
                entity.getAttributes().stream().map(AttributeMapper::mapToDomainModel).toList()));
    }
}
