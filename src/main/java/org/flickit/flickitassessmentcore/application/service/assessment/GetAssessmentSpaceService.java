package org.flickit.flickitassessmentcore.application.service.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.assessment.GetAssessmentSpaceUseCase;
import org.flickit.flickitassessmentcore.application.port.out.assessment.LoadSpaceIdPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetAssessmentSpaceService implements GetAssessmentSpaceUseCase {

    private final LoadSpaceIdPort loadAssessmentSpacePort;

    @Override
    public Result getAssessmentSpace(Param param) {
        Long spaceId = loadAssessmentSpacePort.loadSpaceIdByAssessmentId(param.getAssessmentId());
        return new Result(spaceId);
    }
}
