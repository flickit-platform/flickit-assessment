package org.flickit.assessment.core.application.service.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.space.SpaceType;
import org.flickit.assessment.common.config.AppSpecProperties;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.common.exception.UpgradeRequiredException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.core.application.domain.Space;
import org.flickit.assessment.core.application.port.in.assessment.MoveAssessmentUseCase;
import org.flickit.assessment.core.application.port.out.assessment.CountAssessmentsPort;
import org.flickit.assessment.core.application.port.out.assessment.MoveAssessmentPort;
import org.flickit.assessment.core.application.port.out.space.LoadSpacePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.MOVE_ASSESSMENT;
import static org.flickit.assessment.common.error.ErrorMessageKey.*;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;

@Service
@Transactional
@RequiredArgsConstructor
public class MoveAssessmentService implements MoveAssessmentUseCase {

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final LoadSpacePort loadSpacePort;
    private final MoveAssessmentPort moveAssessmentPort;
    private final CountAssessmentsPort countAssessmentsPort;
    private final AppSpecProperties appSpecProperties;

    @Override
    public void moveAssessment(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), MOVE_ASSESSMENT))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        validateSpace(param);
        moveAssessmentPort.moveAssessment(param.getAssessmentId(), param.getTargetSpaceId());
    }

    private void validateSpace(Param param) {
        UUID currentUserId = param.getCurrentUserId();
        Long targetSpaceId = param.getTargetSpaceId();

        var currentSpace = loadSpacePort.loadAssessmentSpace(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(COMMON_SPACE_ID_NOT_FOUND));

        if (targetSpaceId.equals(currentSpace.getId()))
            throw new ValidationException(MOVE_ASSESSMENT_TARGET_SPACE_INVALID);

        var targetSpace = loadSpacePort.loadSpace(targetSpaceId)
            .orElseThrow(() -> new ResourceNotFoundException(MOVE_ASSESSMENT_TARGET_SPACE_NOT_FOUND));

        if (!targetSpace.getOwnerId().equals(param.getCurrentUserId()))
            throw new AccessDeniedException(MOVE_ASSESSMENT_USER_NOT_ALLOWED);

        validateTargetSpace(targetSpace);
        if (!currentUserId.equals(currentSpace.getOwnerId()) || !currentUserId.equals(targetSpace.getOwnerId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void validateTargetSpace(Space targetSpace) {
        if (targetSpace.getType().equals(SpaceType.BASIC) &&
            countAssessmentsPort.countSpaceAssessments(targetSpace.getId()) >= appSpecProperties.getSpace().getMaxBasicSpaceAssessments())
            throw new UpgradeRequiredException(MOVE_ASSESSMENT_TARGET_SPACE_ASSESSMENTS_MAX);
    }
}
