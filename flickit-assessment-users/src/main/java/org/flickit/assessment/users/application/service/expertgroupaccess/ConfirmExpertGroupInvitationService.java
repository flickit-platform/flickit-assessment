package org.flickit.assessment.users.application.service.expertgroupaccess;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.users.application.port.in.expertgroupaccess.ConfirmExpertGroupInvitationUseCase;
import org.flickit.assessment.users.application.port.out.expertgroupaccess.CheckInviteInputDataValidityPort;
import org.flickit.assessment.users.application.port.out.expertgroupaccess.CheckInviteTokenExpiryPort;
import org.flickit.assessment.users.application.port.out.expertgroupaccess.ConfirmExpertGroupInvitationPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.flickit.assessment.users.common.ErrorMessageKey.CONFIRM_EXPERT_GROUP_INVITATION_INVITE_TOKEN_EXPIRED;
import static org.flickit.assessment.users.common.ErrorMessageKey.CONFIRM_EXPERT_GROUP_INVITATION_INPUT_DATA_INVALID;

@Service
@Transactional
@RequiredArgsConstructor
public class ConfirmExpertGroupInvitationService implements ConfirmExpertGroupInvitationUseCase {

    private final CheckInviteInputDataValidityPort checkInviteInputDataValidityPort;
    private final CheckInviteTokenExpiryPort checkInviteTokenExpiryPort;
    private final ConfirmExpertGroupInvitationPort confirmExpertGroupInvitationPort;

    public void confirmInvitation(Param param) {
        boolean inputDataIsValid = checkInviteInputDataValidityPort
            .checkInputData(param.getExpertGroupId(), param.getUserId(), param.getInviteToken());
        if (!inputDataIsValid)
            throw new ResourceNotFoundException(CONFIRM_EXPERT_GROUP_INVITATION_INPUT_DATA_INVALID);

        boolean tokenIsValid = checkInviteTokenExpiryPort.isInviteTokenValid(param.getInviteToken());
        if (!tokenIsValid)
            throw new ResourceNotFoundException(CONFIRM_EXPERT_GROUP_INVITATION_INVITE_TOKEN_EXPIRED);
        confirmExpertGroupInvitationPort.confirmInvitation(param.getInviteToken());
    }
}
