package org.flickit.flickitassessmentcore.application.service.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.assessment.GetAssessmentSpaceUseCase;
import org.flickit.flickitassessmentcore.application.port.out.assessment.GetAssessmentSpacePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetAssessmentSpaceService implements GetAssessmentSpaceUseCase {

    private final GetAssessmentSpacePort getAssessmentSpacePort;

    @Override
    public Result getAssessmentSpace(Param param) {
        Long spaceId = getAssessmentSpacePort.getSpaceIdByAssessmentId(param.getAssessmentId());
        return new Result(spaceId);
    }
}
