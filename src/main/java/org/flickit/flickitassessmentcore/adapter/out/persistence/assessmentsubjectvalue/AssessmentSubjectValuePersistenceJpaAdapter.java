package org.flickit.flickitassessmentcore.adapter.out.persistence.assessmentsubjectvalue;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.out.assessmentsubjectvalue.SaveAssessmentSubjectValuePort;
import org.flickit.flickitassessmentcore.domain.AssessmentSubjectValue;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AssessmentSubjectValuePersistenceJpaAdapter implements SaveAssessmentSubjectValuePort {

    private final AssessmentSubjectValueJpaRepository repository;

    @Override
    public void saveAssessmentSubjectValue(AssessmentSubjectValue subjectValue) {
        repository.save(AssessmentSubjectValueMapper.mapToJpaEntity(subjectValue));
    }
}
