package org.flickit.assessment.core.adapter.out.persistence.assessmentsubjectvalue;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.port.out.assessmentsubjectvalue.SaveAssessmentSubjectValuePort;
import org.flickit.assessment.core.domain.AssessmentSubjectValue;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AssessmentSubjectValuePersistenceAdapter implements SaveAssessmentSubjectValuePort {

    private final AssessmentSubjectValueRepository repository;

    @Override
    public void saveAssessmentSubjectValue(AssessmentSubjectValue subjectValue) {
        repository.save(AssessmentSubjectValueMapper.mapToJpaEntity(subjectValue));
    }
}
