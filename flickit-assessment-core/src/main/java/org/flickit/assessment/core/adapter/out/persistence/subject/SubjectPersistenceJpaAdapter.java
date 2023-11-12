package org.flickit.assessment.core.adapter.out.persistence.subject;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.port.out.subject.LoadAssessmentKitSubjectModelsByKitPort;
import org.flickit.assessment.data.jpa.subject.SubjectJpaRepository;
import org.flickit.assessment.kit.domain.Subject;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SubjectPersistenceJpaAdapter implements LoadAssessmentKitSubjectModelsByKitPort {

    private final SubjectJpaRepository repository;

    @Override
    public List<Subject> load(Long assessmentKitId) {
        var subjectJpaEntities = repository.loadByAssessmentKitId(assessmentKitId);
        return subjectJpaEntities.stream()
            .map(SubjectMapper::mapToKitDomainModel)
            .toList();
    }
}
