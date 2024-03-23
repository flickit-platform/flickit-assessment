package org.flickit.assessment.users.application.service.expertgroupaccess;

import lombok.AllArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.users.application.domain.ExpertGroupAccessStatus;
import org.flickit.assessment.users.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.users.application.port.out.expertgroupaccess.InviteExpertGroupMemberPort;
import org.flickit.assessment.users.application.port.out.mail.SendExpertGroupInviteMailPort;
import org.flickit.assessment.users.application.port.in.expertgroupaccess.InviteExpertGroupMemberUseCase;
import org.flickit.assessment.users.application.port.out.user.LoadUserEmailByUserIdPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;


import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.users.common.ErrorMessageKey.EXPERT_GROUP_ID_NOT_FOUND;

@Service
@Transactional
@AllArgsConstructor
public class InviteExpertGroupMemberService implements InviteExpertGroupMemberUseCase {

    private static final Duration EXPIRY_DURATION = Duration.ofDays(7);

    private final LoadUserEmailByUserIdPort loadUserEmailByUserIdPort;
    private final InviteExpertGroupMemberPort inviteExpertGroupMemberPort;
    private final SendExpertGroupInviteMailPort sendExpertGroupInviteMailPort;
    private final LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    @Override
    public void inviteMember(Param param) {
        validateCurrentUser(param.getExpertGroupId(), param.getCurrentUserId());
        UUID inviteToken = UUID.randomUUID();
        var inviteDate = LocalDateTime.now();
        var inviteExpirationDate = inviteDate.plusDays(EXPIRY_DURATION.toDays());
        String email = loadUserEmailByUserIdPort.loadEmail(param.getUserId());

        var result = inviteExpertGroupMemberPort.invite(toParam(param, inviteDate, inviteExpirationDate, inviteToken));

        if (result != null)
            new Thread(() ->
                sendExpertGroupInviteMailPort.sendInvite(email, inviteToken)).start();
    }

    private void validateCurrentUser(Long expertGroupId, UUID currentUserId) {
        UUID expertGroupOwnerId = loadExpertGroupOwnerPort.loadOwnerId(expertGroupId)
            .orElseThrow(() -> new ResourceNotFoundException(EXPERT_GROUP_ID_NOT_FOUND));
        if (!Objects.equals(expertGroupOwnerId, currentUserId))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
    }

    private InviteExpertGroupMemberPort.Param toParam(Param param, LocalDateTime inviteDate,
                                                      LocalDateTime inviteExpirationDate, UUID inviteToken) {
        return new InviteExpertGroupMemberPort.Param(
            param.getExpertGroupId(),
            param.getUserId(),
            inviteDate,
            inviteExpirationDate,
            inviteToken,
            ExpertGroupAccessStatus.PENDING,
            param.getCurrentUserId());
    }
}
