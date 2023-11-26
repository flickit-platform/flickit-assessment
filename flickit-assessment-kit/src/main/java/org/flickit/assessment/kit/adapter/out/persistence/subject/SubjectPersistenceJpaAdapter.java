package org.flickit.assessment.kit.adapter.out.persistence.subject;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaEntity;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaRepository;
import org.flickit.assessment.kit.application.domain.Subject;
import org.flickit.assessment.kit.application.port.out.subject.LoadSubjectByKitPort;
import org.flickit.assessment.kit.application.port.out.subject.UpdateSubjectsPort;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@RequiredArgsConstructor
public class SubjectPersistenceJpaAdapter implements
    LoadSubjectByKitPort,
    UpdateSubjectsPort {

    private final SubjectJpaRepository repo;

    @Override
    public List<Subject> loadByKitId(Long assessmentKitId) {
        List<SubjectJpaEntity> entities = repo.findAllByAssessmentKit_Id(assessmentKitId);
        return entities.stream().map(SubjectMapper::mapToDomainModel).toList();
    }

    @Override
    public void updateSubject(Param param) {
        repo.updateByCodeAndAssessmentKitId(param.kitId(),
            param.code(),
            param.Title(),
            param.Description(),
            param.Index(),
            param.lastModificationTime());
    }
}
