package org.flickit.assessment.users.application.service.spaceuseraccess;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.users.application.port.in.spaceuseraccess.DeleteSpaceMemberUseCase;
import org.flickit.assessment.users.application.port.out.assessmentuserrole.DeleteSpaceAssessmentUserRolesPort;
import org.flickit.assessment.users.application.port.out.space.LoadSpaceOwnerPort;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.CheckSpaceAccessPort;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.DeleteSpaceMemberPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.users.common.ErrorMessageKey.DELETE_SPACE_MEMBER_USER_ID_NOT_FOUND;

@Service
@Transactional
@RequiredArgsConstructor
public class DeleteSpaceMemberService implements DeleteSpaceMemberUseCase {

    private final LoadSpaceOwnerPort loadSpaceOwnerPort;
    private final CheckSpaceAccessPort checkSpaceAccessPort;
    private final DeleteSpaceMemberPort deleteSpaceMemberPort;
    private final DeleteSpaceAssessmentUserRolesPort deleteSpaceAssessmentUserRolesPort;

    @Override
    public void deleteMember(Param param) {
        validateCurrentUser(param.getSpaceId(), param.getUserId(), param.getCurrentUserId());
        var access = checkSpaceAccessPort.checkIsMember(param.getSpaceId(), param.getUserId());
        if (!access)
            throw new ResourceNotFoundException(DELETE_SPACE_MEMBER_USER_ID_NOT_FOUND);

        deleteSpaceAssessmentUserRolesPort.delete(param.getUserId(), param.getSpaceId());
        deleteSpaceMemberPort.delete(param.getSpaceId(), param.getUserId());
    }

    private void validateCurrentUser(Long spaceId, UUID userId, UUID currentUserId) {
        UUID spaceOwnerId = loadSpaceOwnerPort.loadOwnerId(spaceId);
        if (!Objects.equals(spaceOwnerId, currentUserId)
            || Objects.equals(spaceOwnerId, userId))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
    }
}
