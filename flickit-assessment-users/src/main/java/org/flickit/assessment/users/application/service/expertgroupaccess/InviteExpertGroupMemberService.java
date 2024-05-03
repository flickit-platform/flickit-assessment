package org.flickit.assessment.users.application.service.expertgroupaccess;

import lombok.AllArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceAlreadyExistsException;
import org.flickit.assessment.users.application.domain.ExpertGroupAccessStatus;
import org.flickit.assessment.users.application.port.in.expertgroupaccess.InviteExpertGroupMemberUseCase;
import org.flickit.assessment.users.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.users.application.port.out.expertgroupaccess.InviteExpertGroupMemberPort;
import org.flickit.assessment.users.application.port.out.expertgroupaccess.LoadExpertGroupMemberStatusPort;
import org.flickit.assessment.users.application.port.out.mail.SendExpertGroupInviteMailPort;
import org.flickit.assessment.users.application.port.out.user.LoadUserEmailByUserIdPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.users.common.ErrorMessageKey.INVITE_EXPERT_GROUP_MEMBER_EXPERT_GROUP_ID_USER_ID_DUPLICATE;

@Service
@Transactional
@AllArgsConstructor
public class InviteExpertGroupMemberService implements InviteExpertGroupMemberUseCase {

    private static final Duration EXPIRY_DURATION = Duration.ofDays(7);

    private final LoadUserEmailByUserIdPort loadUserEmailByUserIdPort;
    private final LoadExpertGroupMemberStatusPort loadExpertGroupMemberPort;
    private final InviteExpertGroupMemberPort inviteExpertGroupMemberPort;
    private final SendExpertGroupInviteMailPort sendExpertGroupInviteMailPort;
    private final LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    @Override
    public void inviteMember(Param param) {
        validateCurrentUser(param.getExpertGroupId(), param.getCurrentUserId());
        Optional<Integer> memberStatus = loadExpertGroupMemberPort.getMemberStatus(param.getExpertGroupId(), param.getUserId());

        if (memberStatus.isPresent() && (memberStatus.get() == ExpertGroupAccessStatus.ACTIVE.ordinal()))
            throw new ResourceAlreadyExistsException(INVITE_EXPERT_GROUP_MEMBER_EXPERT_GROUP_ID_USER_ID_DUPLICATE);

        var inviteToken = UUID.randomUUID();
        var inviteDate = LocalDateTime.now();
        var inviteExpirationDate = inviteDate.plusDays(EXPIRY_DURATION.toDays());
        var email = loadUserEmailByUserIdPort.loadEmail(param.getUserId());

        inviteExpertGroupMemberPort.invite(toParam(param, inviteDate, inviteExpirationDate, inviteToken));
        sendExpertGroupInviteMailPort.inviteToExpertGroup(email, param.getExpertGroupId(), inviteToken);
    }

    private void validateCurrentUser(Long expertGroupId, UUID currentUserId) {
        UUID expertGroupOwnerId = loadExpertGroupOwnerPort.loadOwnerId(expertGroupId);
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
