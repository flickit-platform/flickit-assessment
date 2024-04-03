package org.flickit.assessment.users.application.service.expertgroupaccess;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceAlreadyExistsException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.users.application.domain.ExpertGroupAccess;
import org.flickit.assessment.users.application.domain.ExpertGroupAccessStatus;
import org.flickit.assessment.users.application.port.in.expertgroupaccess.ConfirmExpertGroupInvitationUseCase;
import org.flickit.assessment.users.application.port.out.expertgroupaccess.LoadExpertGroupAccessPort;
import org.flickit.assessment.users.application.port.out.expertgroupaccess.ConfirmExpertGroupInvitationPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.flickit.assessment.users.common.ErrorMessageKey.*;

@Service
@Transactional
@RequiredArgsConstructor
public class ConfirmExpertGroupInvitationService implements ConfirmExpertGroupInvitationUseCase {

    private final LoadExpertGroupAccessPort loadExpertGroupAccessPort;
    private final ConfirmExpertGroupInvitationPort confirmExpertGroupInvitationPort;

    public void confirmInvitation(Param param) {
        ExpertGroupAccess expertGroupAccess = loadExpertGroupAccessPort
            .loadExpertGroupAccess(param.getExpertGroupId(), param.getCurrentUserId());

        if (expertGroupAccess.getInviteToken() == null || expertGroupAccess.getInviteExpirationDate() == null)
            throw new ResourceNotFoundException(CONFIRM_EXPERT_GROUP_INVITATION_LINK_INVALID);

        if (expertGroupAccess.getStatus() == ExpertGroupAccessStatus.ACTIVE.ordinal())
            throw new ResourceAlreadyExistsException(CONFIRM_EXPERT_GROUP_INVITATION_USER_ID_DUPLICATE);

        if (!expertGroupAccess.getInviteToken().equals(param.getInviteToken()))
            throw new ResourceNotFoundException(CONFIRM_EXPERT_GROUP_INVITATION_INVITE_TOKEN_INVALID);

        if (LocalDateTime.now().isAfter(expertGroupAccess.getInviteExpirationDate()))
            throw new ValidationException(CONFIRM_EXPERT_GROUP_INVITATION_INVITE_TOKEN_EXPIRED);

        confirmExpertGroupInvitationPort.confirmInvitation(param.getExpertGroupId(), param.getCurrentUserId());
    }
}
