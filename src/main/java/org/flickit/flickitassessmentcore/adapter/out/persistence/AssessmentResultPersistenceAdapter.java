package org.flickit.flickitassessmentcore.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.adapter.out.persistence.repository.AssessmentResultRepository;
import org.flickit.flickitassessmentcore.application.port.out.SaveAssessmentResultPort;
import org.flickit.flickitassessmentcore.domain.AssessmentResult;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AssessmentResultPersistenceAdapter implements SaveAssessmentResultPort {

    private final AssessmentResultRepository assessmentResultRepository;

    @Override
    public void saveAssessmentResult(AssessmentResult assessmentResult) {
        assessmentResultRepository.save(assessmentResult);
    }
}
