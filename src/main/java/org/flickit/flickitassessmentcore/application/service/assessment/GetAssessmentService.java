package org.flickit.flickitassessmentcore.application.service.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.domain.Assessment;
import org.flickit.flickitassessmentcore.application.port.in.assessment.GetAssessmentUseCase;
import org.flickit.flickitassessmentcore.application.port.out.assessment.GetAssessmentPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetAssessmentService implements GetAssessmentUseCase {

    private final GetAssessmentPort getAssessmentPort;

    @Override
    public Result getAssessment(Param param) {
        Assessment assessment = getAssessmentPort.getAssessmentById(param.getAssessmentId());
        return new Result(
            assessment.getId(),
            assessment.getSpaceId(),
            assessment.getAssessmentKit().getId());
    }
}
