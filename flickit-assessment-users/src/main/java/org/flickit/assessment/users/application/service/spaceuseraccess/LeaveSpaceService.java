package org.flickit.assessment.users.application.service.spaceuseraccess;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.users.application.port.in.spaceuseraccess.LeaveSpaceUseCase;
import org.flickit.assessment.users.application.port.out.assessmentuserrole.DeleteSpaceAssessmentUserRolesPort;
import org.flickit.assessment.users.application.port.out.space.LoadSpaceOwnerPort;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.CheckSpaceAccessPort;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.DeleteSpaceMemberPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.users.common.ErrorMessageKey.LEAVE_SPACE_OWNER_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class LeaveSpaceService implements LeaveSpaceUseCase {

    private final CheckSpaceAccessPort checkSpaceAccessPort;
    private final LoadSpaceOwnerPort loadSpaceOwnerPort;
    private final DeleteSpaceMemberPort deleteSpaceMemberPort;
    private final DeleteSpaceAssessmentUserRolesPort deleteSpaceAssessmentUserRolesPort;

    @Override
    public void leaveSpace(Param param) {
        if (!checkSpaceAccessPort.checkIsMember(param.getId(), param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        UUID spaceOwnerId = loadSpaceOwnerPort.loadOwnerId(param.getId());
        if (Objects.equals(spaceOwnerId, param.getCurrentUserId()))
            throw new ValidationException(LEAVE_SPACE_OWNER_NOT_ALLOWED);

        deleteSpaceAssessmentUserRolesPort.delete(param.getCurrentUserId(), param.getId());
        deleteSpaceMemberPort.delete(param.getId(), param.getCurrentUserId());
    }
}
