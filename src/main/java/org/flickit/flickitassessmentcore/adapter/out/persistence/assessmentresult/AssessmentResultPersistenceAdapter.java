package org.flickit.flickitassessmentcore.adapter.out.persistence.assessmentresult;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.adapter.out.persistence.assessmentresult.exception.AssessmentResultNotFound;
import org.flickit.flickitassessmentcore.application.port.out.LoadAssessmentResultPort;
import org.flickit.flickitassessmentcore.application.port.out.SaveAssessmentResultPort;
import org.flickit.flickitassessmentcore.domain.AssessmentResult;
import org.springframework.stereotype.Component;

import java.util.UUID;

@RequiredArgsConstructor
@Component
public class AssessmentResultPersistenceAdapter implements SaveAssessmentResultPort, LoadAssessmentResultPort {

    private final AssessmentResultRepository assessmentResultRepository;

    @Override
    public void saveAssessmentResult(AssessmentResult assessmentResult) {
        assessmentResultRepository.save(AssessmentResultMapper.mapToJpaEntity(assessmentResult));
    }

    @Override
    public AssessmentResult loadResult(UUID resultId) {
        return AssessmentResultMapper.mapToDomainModel(
            assessmentResultRepository.findById(resultId).orElseThrow(
                () -> new AssessmentResultNotFound("Assessment Result with id [" + resultId + "] not found!")));
    }
}
