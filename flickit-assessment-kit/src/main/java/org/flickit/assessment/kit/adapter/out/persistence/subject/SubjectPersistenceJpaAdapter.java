package org.flickit.assessment.kit.adapter.out.persistence.subject;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.data.jpa.kit.assessmentkit.AssessmentKitJpaRepository;
import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaRepository;
import org.flickit.assessment.data.jpa.kit.question.QuestionJpaRepository;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaRepository;
import org.flickit.assessment.kit.application.domain.Subject;
import org.flickit.assessment.kit.application.port.in.subject.GetSubjectDetailUseCase;
import org.flickit.assessment.kit.application.port.out.subject.CreateSubjectPort;
import org.flickit.assessment.kit.application.port.out.subject.LoadSubjectsPort;
import org.flickit.assessment.kit.application.port.out.subject.LoadSubjectDetailPort;
import org.flickit.assessment.kit.application.port.out.subject.UpdateSubjectPort;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_SUBJECT_DETAIL_SUBJECT_ID_NOT_FOUND;
import static org.flickit.assessment.kit.common.ErrorMessageKey.KIT_ID_NOT_FOUND;


@Component
@RequiredArgsConstructor
public class SubjectPersistenceJpaAdapter implements
    UpdateSubjectPort,
    CreateSubjectPort,
    LoadSubjectsPort,
    LoadSubjectDetailPort {

    private final SubjectJpaRepository repository;
    private final AssessmentKitJpaRepository assessmentKitRepository;
    private final QuestionJpaRepository questionRepository;
    private final AttributeJpaRepository attributeRepository;

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
    public List<Subject> loadSubjects(long kitVersionId) {
        return repository.findAllByKitVersionId(kitVersionId).stream()
            .map(e -> SubjectMapper.mapToDomainModel(e, null))
            .toList();
    }

    @Override
    public GetSubjectDetailUseCase.Result loadByIdAndKitId(Long subjectId, Long kitId) {
        var kitVersionId = assessmentKitRepository.findById(kitId)
            .orElseThrow(() -> new ResourceNotFoundException(KIT_ID_NOT_FOUND))
            .getKitVersionId();

        var subject = repository.findByIdAndKitVersionId(subjectId, kitVersionId)
            .orElseThrow(() -> new ResourceNotFoundException(GET_SUBJECT_DETAIL_SUBJECT_ID_NOT_FOUND));
        var questionCount = questionRepository.countDistinctBySubjectId(subjectId);
        var attributes = attributeRepository.findAllBySubjectId(subjectId).stream()
            .map(a -> new GetSubjectDetailUseCase.Attribute(a.getId(), a.getIndex(), a.getTitle()))
            .toList();
        return new GetSubjectDetailUseCase.Result(questionCount, subject.getDescription(), attributes);
    }
}
