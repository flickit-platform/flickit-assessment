package org.flickit.assessment.users.application.service.expertgroupaccess;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.users.application.port.out.expertgroupaccess.CheckInviteInputDataValidityPort;
import org.flickit.assessment.users.application.port.out.expertgroupaccess.CheckInviteTokenExpiryPort;
import org.flickit.assessment.users.application.port.out.expertgroupaccess.ConfirmExpertGroupInvitationPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.flickit.assessment.users.common.ErrorMessageKey.CONFIRM_EXPERT_GROUP_INVITATION_INVITE_TOKEN_EXPIRED;
import static org.flickit.assessment.users.common.ErrorMessageKey.CONFIRM_EXPERT_GROUP_INVITATION_INPUT_DATA_INVALID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ConfirmExpertGroupInvitationService {

    private CheckInviteInputDataValidityPort checkInviteInputDataValidityPort;
    private CheckInviteTokenExpiryPort checkInviteTokenExpiryPort;
    private ConfirmExpertGroupInvitationPort confirmExpertGroupInvitationPort;

    public void confirmInvitation(long expertGroupId, UUID userId, UUID inviteToken) {
        boolean inputDataIsValid = checkInviteInputDataValidityPort.checkInputData(expertGroupId, userId, inviteToken);
        if (!inputDataIsValid)
            throw new ResourceNotFoundException(CONFIRM_EXPERT_GROUP_INVITATION_INPUT_DATA_INVALID);

        boolean tokenIsValid = checkInviteTokenExpiryPort.isInviteTokenValid(inviteToken);
        if (!tokenIsValid)
            throw new ResourceNotFoundException(CONFIRM_EXPERT_GROUP_INVITATION_INVITE_TOKEN_EXPIRED);
        confirmExpertGroupInvitationPort.confirmInvitation(inviteToken);
    }
}
