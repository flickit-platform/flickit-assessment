package org.flickit.assessment.users.application.service.expertgroupaccess;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.users.application.port.out.expertgroupaccess.CheckInviteTokenValidationPort;
import org.flickit.assessment.users.application.port.out.expertgroupaccess.ConfirmExpertGroupInvitationPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ConfirmExpertGroupInvitationService {

    private CheckInviteTokenValidationPort checkInviteTokenValidationPort;
    private ConfirmExpertGroupInvitationPort confirmExpertGroupInvitationPort;

    public void confirmInvitation(long expertGroupId, UUID userId, UUID inviteToken) {

        checkInviteTokenValidationPort.checkToken(expertGroupId, userId, inviteToken);
        confirmExpertGroupInvitationPort.confirmInvitation(inviteToken);
    }
}
