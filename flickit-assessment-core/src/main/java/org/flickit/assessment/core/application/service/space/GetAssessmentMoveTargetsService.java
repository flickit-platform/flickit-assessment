package org.flickit.assessment.core.application.service.space;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.port.in.space.GetAssessmentMoveTargetsUseCase;
import org.flickit.assessment.core.application.port.out.space.LoadSpacePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.flickit.assessment.core.common.ErrorMessageKey.GET_ASSESSMENT_MOVE_TARGETS_SPACE_NOT_FOUND;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetAssessmentMoveTargetsService implements GetAssessmentMoveTargetsUseCase {

    private final LoadSpacePort loadSpacePort;

    @Override
    public Result getSpaceList(Param param) {
        var space = loadSpacePort.loadByAssessmentId(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(GET_ASSESSMENT_MOVE_TARGETS_SPACE_NOT_FOUND));
        return null;
    }
}
