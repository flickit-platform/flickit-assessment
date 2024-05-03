package org.flickit.assessment.kit.adapter.out.persistence.subject;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.data.jpa.kit.assessmentkit.AssessmentKitJpaRepository;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaRepository;
import org.flickit.assessment.kit.adapter.out.persistence.attribute.AttributeMapper;
import org.flickit.assessment.kit.application.domain.Subject;
import org.flickit.assessment.kit.application.port.out.subject.CreateSubjectPort;
import org.flickit.assessment.kit.application.port.out.subject.LoadSubjectPort;
import org.flickit.assessment.kit.application.port.out.subject.LoadSubjectsPort;
import org.flickit.assessment.kit.application.port.out.subject.UpdateSubjectPort;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.flickit.assessment.kit.adapter.out.persistence.subject.SubjectMapper.mapToDomainModel;
import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_KIT_SUBJECT_DETAIL_SUBJECT_ID_NOT_FOUND;
import static org.flickit.assessment.kit.common.ErrorMessageKey.KIT_ID_NOT_FOUND;


@Component
@RequiredArgsConstructor
public class SubjectPersistenceJpaAdapter implements
    UpdateSubjectPort,
    CreateSubjectPort,
    LoadSubjectsPort,
    LoadSubjectPort {

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
    public Subject load(long kitId, long subjectId) {
        var subject = repository.findByIdAndKitId(kitId, subjectId)
            .orElseThrow(() -> new ResourceNotFoundException(GET_KIT_SUBJECT_DETAIL_SUBJECT_ID_NOT_FOUND));
        return mapToDomainModel(subject,
            subject.getAttributes().stream().map(AttributeMapper::mapToDomainModel).toList());
    }
}
