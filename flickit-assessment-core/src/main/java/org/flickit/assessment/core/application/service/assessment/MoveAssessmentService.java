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
import org.flickit.assessment.core.application.port.out.assessment.UpdateAssessmentPort;
import org.flickit.assessment.core.application.port.out.assessmentuserrole.LoadAssessmentUsersPort;
import org.flickit.assessment.core.application.port.out.space.LoadSpacePort;
import org.flickit.assessment.core.application.port.out.spaceuseraccess.CreateSpaceUserAccessPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.MOVE_ASSESSMENT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_SPACE_ID_NOT_FOUND;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;

@Service
@Transactional
@RequiredArgsConstructor
public class MoveAssessmentService implements MoveAssessmentUseCase {

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final LoadSpacePort loadSpacePort;
    private final UpdateAssessmentPort updateAssessmentPort;
    private final CountAssessmentsPort countAssessmentsPort;
    private final AppSpecProperties appSpecProperties;
    private final LoadAssessmentUsersPort loadAssessmentUsersPort;
    private final CreateSpaceUserAccessPort createSpaceUserAccessPort;

    @Override
    public void moveAssessment(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), MOVE_ASSESSMENT))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var currentSpace = loadSpacePort.loadByAssessmentId(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(COMMON_SPACE_ID_NOT_FOUND));

        if (!param.getCurrentUserId().equals(currentSpace.getOwnerId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var targetSpaceId = param.getTargetSpaceId();
        if (targetSpaceId.equals(currentSpace.getId()))
            throw new ValidationException(MOVE_ASSESSMENT_TARGET_SPACE_INVALID);

        var targetSpace = loadSpacePort.loadById(targetSpaceId)
            .orElseThrow(() -> new ResourceNotFoundException(MOVE_ASSESSMENT_TARGET_SPACE_NOT_FOUND));

        validateTargetSpace(targetSpace, param.getCurrentUserId(), param.getAssessmentId());
        updateAssessmentPort.updateSpace(param.getAssessmentId(), targetSpaceId);
        createAssessmentUsersAccessOnTargetSpace(param.getAssessmentId(), targetSpaceId, param.getCurrentUserId());
    }

    private void validateTargetSpace(Space targetSpace, UUID currentUserId, UUID assessmentId) {
        if (!targetSpace.getOwnerId().equals(currentUserId))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        if (targetSpace.getType().equals(SpaceType.BASIC) &&
            countAssessmentsPort.countSpaceAssessments(targetSpace.getId()) >= appSpecProperties.getSpace().getMaxBasicSpaceAssessments())
            throw new UpgradeRequiredException(MOVE_ASSESSMENT_TARGET_SPACE_ASSESSMENTS_MAX);

        if (targetSpace.getType().equals(SpaceType.BASIC)
            && targetSpace.isDefault() && loadAssessmentUsersPort.hasNonSpaceOwnerAccess(assessmentId))
            throw new ValidationException(MOVE_ASSESSMENT_ASSESSMENT_NON_OWNER_ACCESS_NOT_ALLOWED);
    }

    private void createAssessmentUsersAccessOnTargetSpace(UUID assessmentId, long targetSpaceId, UUID currentUserId) {
        List<UUID> userIds = loadAssessmentUsersPort.loadAllUserIds(assessmentId);
        var createParam = new CreateSpaceUserAccessPort.CreateAllParam(targetSpaceId, userIds, currentUserId, LocalDateTime.now());
        createSpaceUserAccessPort.persistByUserIds(createParam);
    }
}
