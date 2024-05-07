package org.flickit.assessment.users.application.service.spaceuseraccess;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.users.application.port.in.spaceuseraccess.LeaveSpaceMemberUseCase;
import org.flickit.assessment.users.application.port.out.space.LoadSpaceOwnerPort;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.CheckSpaceAccessPort;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.DeleteSpaceUserAccessPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class LeaveSpaceMemberService implements LeaveSpaceMemberUseCase {

    private final CheckSpaceAccessPort checkSpaceAccessPort;
    private final DeleteSpaceUserAccessPort deleteSpaceUserAccessPort;
    private final LoadSpaceOwnerPort loadSpaceOwnerPort;

    @Override
    public void leaveMember(Param param) {
        if (!checkSpaceAccessPort.checkIsMember(param.getId(), param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        UUID spaceOwnerId = loadSpaceOwnerPort.loadOwnerId(param.getId());
        if (Objects.equals(spaceOwnerId, param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        deleteSpaceUserAccessPort.deleteAccess(param.getId(), param.getCurrentUserId());
    }
}
