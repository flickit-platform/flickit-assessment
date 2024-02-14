package org.flickit.assessment.kit.application.service.expertgroupaccess;

import lombok.AllArgsConstructor;
import org.flickit.assessment.data.jpa.kit.expertgroupaccess.ExpertGroupAccessStatus;
import org.flickit.assessment.kit.application.port.in.expertgroupaccess.InviteExpertGroupMemberUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.InviteExpertGroupMemberPort;
import org.flickit.assessment.kit.application.port.out.mail.SendExpertGroupInvitationMailPort;
import org.flickit.assessment.kit.application.port.out.user.LoadUserEmailByUserIdPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.InviteTokenCheckPort;

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
    private final InviteTokenCheckPort inviteTokenCheckPort;
    private static final Duration EXPIRY_DURATION = Duration.ofDays(7);

    @Override
    public void inviteMember(Param param) {
        UUID inviteToken = UUID.randomUUID();
        var inviteDate = LocalDateTime.now();
        var inviteExpirationDate = inviteDate.plusDays(EXPIRY_DURATION.toDays());
        String email = loadUserEmailByUserIdPort.loadEmail(param.getUserId());

        inviteExpertGroupMemberPort.persist(toParam(param, inviteDate, inviteExpirationDate, inviteToken));
        boolean isInserted = inviteTokenCheckPort.checkInviteToken(inviteToken);

        if (isInserted)
            new Thread(() ->
                sendExpertGroupInvitationMailPort.sendInviteExpertGroupMemberEmail(email, inviteToken)).start();
    }

    private InviteExpertGroupMemberPort.Param toParam(Param param, LocalDateTime inviteDate,
                                                      LocalDateTime inviteExpirationDate, UUID inviteToken) {
        return new InviteExpertGroupMemberPort.Param(
            param.getExpertGroupId(),
            param.getUserId(),
            param.getCurrentUserId(),
            inviteDate,
            inviteExpirationDate,
            inviteToken,
            ExpertGroupAccessStatus.PENDING
        );
    }
}
