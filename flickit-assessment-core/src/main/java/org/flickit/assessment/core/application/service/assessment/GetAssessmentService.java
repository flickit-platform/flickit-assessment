package org.flickit.assessment.core.application.service.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.Assessment;
import org.flickit.assessment.core.application.port.in.assessment.GetAssessmentUseCase;
import org.flickit.assessment.core.application.port.out.assessment.GetAssessmentPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.flickit.assessment.core.common.ErrorMessageKey.GET_ASSESSMENT_ASSESSMENT_ID_NOT_FOUND;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetAssessmentService implements GetAssessmentUseCase {

    private final GetAssessmentPort getAssessmentPort;

    @Override
    public Result getAssessment(Param param) {
        Assessment assessment = getAssessmentPort.getAssessmentById(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(GET_ASSESSMENT_ASSESSMENT_ID_NOT_FOUND));
        return new Result(
            assessment.getId(),
            assessment.getTitle(),
            assessment.getSpaceId(),
            assessment.getAssessmentKit().getId());
    }
}
