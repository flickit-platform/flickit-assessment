package org.flickit.assessment.kit.application.service.expertgroupaccess;

import lombok.AllArgsConstructor;
import org.flickit.assessment.data.jpa.kit.expertgroupaccess.ExpertGroupAccessStatus;
import org.flickit.assessment.kit.application.port.in.expertgroupaccess.InviteExpertGroupMemberUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.InviteExpertGroupMemberPort;
import org.flickit.assessment.kit.application.port.out.mail.SendExpertGroupInvitationMailPort;
import org.flickit.assessment.kit.application.port.out.user.LoadUserEmailByUserIdPort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class InviteExpertGroupMemberService implements InviteExpertGroupMemberUseCase {

    private final InviteExpertGroupMemberPort inviteExpertGroupMemberPort;
    private final LoadUserEmailByUserIdPort loadUserEmailByUserIdPort;
    private final SendExpertGroupInvitationMailPort sendExpertGroupInvitationMailPort;

    @Override
    public void inviteMember(Param param) {
        UUID inviteToken = UUID.randomUUID();
        String email = loadUserEmailByUserIdPort.loadEmail(param.getUserId());
        inviteExpertGroupMemberPort.invite(toParam(param, inviteToken));
        sendExpertGroupInvitationMailPort.sendInviteExpertGroupMemberEmail(email, inviteToken);
    }

    private InviteExpertGroupMemberPort.Param toParam(Param param, UUID inviteToken) {
        return new InviteExpertGroupMemberPort.Param(
            param.getExpertGroupId(),
            param.getUserId(),
            param.getCurrentUserId(),
            inviteToken,
            ExpertGroupAccessStatus.PENDING
        );
    }
}
