package org.flickit.assessment.users.application.service.expertgroupaccess;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.users.application.port.in.expertgroupaccess.ConfirmExpertGroupInvitationUseCase;
import org.flickit.assessment.users.application.port.out.expertgroupaccess.LoadExpertGroupAccessPort;
import org.flickit.assessment.users.application.port.out.expertgroupaccess.ConfirmExpertGroupInvitationPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.flickit.assessment.users.common.ErrorMessageKey.CONFIRM_EXPERT_GROUP_INVITATION_INVITE_TOKEN_EXPIRED;

@Service
@Transactional
@RequiredArgsConstructor
public class ConfirmExpertGroupInvitationService implements ConfirmExpertGroupInvitationUseCase {

    private final LoadExpertGroupAccessPort loadExpertGroupAccessPort;
    private final ConfirmExpertGroupInvitationPort confirmExpertGroupInvitationPort;

    public void confirmInvitation(Param param) {
        LocalDateTime expirationDate = loadExpertGroupAccessPort
            .loadExpirationDate(param.getExpertGroupId(), param.getInviteToken(), param.getCurrentUserId());

        if (LocalDateTime.now().isAfter(expirationDate))
            throw new ValidationException(CONFIRM_EXPERT_GROUP_INVITATION_INVITE_TOKEN_EXPIRED);

        confirmExpertGroupInvitationPort.confirmInvitation(param.getInviteToken());
    }
}
