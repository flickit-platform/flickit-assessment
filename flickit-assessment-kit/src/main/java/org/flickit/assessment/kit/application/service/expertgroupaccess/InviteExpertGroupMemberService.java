package org.flickit.assessment.kit.application.service.expertgroupaccess;

import lombok.AllArgsConstructor;
import org.flickit.assessment.data.jpa.kit.expertgroupaccess.ExpertGroupAccessStatus;
import org.flickit.assessment.kit.application.port.in.expertgroupaccess.InviteExpertGroupMemberUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.InviteExpertGroupMemberPort;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class InviteExpertGroupMemberService implements InviteExpertGroupMemberUseCase {

    private final InviteExpertGroupMemberPort inviteExpertGroupMemberPort;
    @Override
    public void addMember(Param param) {
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
