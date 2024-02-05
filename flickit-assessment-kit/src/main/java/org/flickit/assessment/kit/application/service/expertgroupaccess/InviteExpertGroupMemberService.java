package org.flickit.assessment.kit.application.service.expertgroupaccess;

import lombok.AllArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.data.jpa.kit.expertgroupaccess.ExpertGroupAccessStatus;
import org.flickit.assessment.kit.application.port.in.expertgroupaccess.InviteExpertGroupMemberUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.InviteExpertGroupMemberPort;
import org.flickit.assessment.kit.application.port.out.user.LoadUserEmailByUserIdPort;
import org.springframework.stereotype.Service;

import static org.flickit.assessment.kit.common.ErrorMessageKey.INVITE_EXPERT_GROUP_MEMBER_EMAIL_NOT_FOUND;

@Service
@AllArgsConstructor
public class InviteExpertGroupMemberService implements InviteExpertGroupMemberUseCase {

    private final InviteExpertGroupMemberPort inviteExpertGroupMemberPort;
    private final LoadUserEmailByUserIdPort loadUserEmailByUserIdPort;

    @Override
    public void addMember(Param param) {
        String email = loadUserEmailByUserIdPort.loadEmail(param.getUserId()).orElseThrow(() ->
            new ResourceNotFoundException(INVITE_EXPERT_GROUP_MEMBER_EMAIL_NOT_FOUND));

        inviteExpertGroupMemberPort.invite(toParam(param));
    }

    private InviteExpertGroupMemberPort.Param toParam(Param param) {
        return new InviteExpertGroupMemberPort.Param(
            param.getExpertGroupId(),
            param.getUserId(),
            param.getCurrentUserId(),
            ExpertGroupAccessStatus.PENDING
        );
    }
}
