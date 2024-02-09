package org.flickit.assessment.kit.application.service.expertgroupaccess;

import lombok.AllArgsConstructor;
import org.flickit.assessment.data.jpa.kit.expertgroupaccess.ExpertGroupAccessStatus;
import org.flickit.assessment.kit.application.port.in.expertgroupaccess.InviteExpertGroupMemberUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.InviteExpertGroupMemberPort;
import org.flickit.assessment.kit.application.port.out.mail.SendExpertGroupInvitationMailPort;
import org.flickit.assessment.kit.application.port.out.user.LoadUserEmailByUserIdPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class InviteExpertGroupMemberService implements InviteExpertGroupMemberUseCase {

    private final InviteExpertGroupMemberPort inviteExpertGroupMemberPort;
    private final LoadUserEmailByUserIdPort loadUserEmailByUserIdPort;
    private final SendExpertGroupInvitationMailPort sendExpertGroupInvitationMailPort;
    private static final Duration EXPIRY_DURATION = Duration.ofDays(7);

    @Override
    public void inviteMember(Param param) {
        UUID inviteToken = UUID.randomUUID();
        var inviteExpirationDate = LocalDateTime.now().plusDays(EXPIRY_DURATION.toDays());
        String email = loadUserEmailByUserIdPort.loadEmail(param.getUserId());
        inviteExpertGroupMemberPort.persist(toParam(param, inviteExpirationDate, inviteToken));
        sendExpertGroupInvitationMailPort.sendInviteExpertGroupMemberEmail(email, inviteToken);
    }

    private InviteExpertGroupMemberPort.Param toParam(Param param, LocalDateTime inviteExpirationDate, UUID inviteToken) {
        return new InviteExpertGroupMemberPort.Param(
            param.getExpertGroupId(),
            param.getUserId(),
            param.getCurrentUserId(),
            inviteExpirationDate,
            inviteToken,
            ExpertGroupAccessStatus.PENDING
        );
    }
}
