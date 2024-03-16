package org.flickit.assessment.users.adapter.out.persistence.expertgroupaccess;

import org.flickit.assessment.users.application.port.out.expertgroupaccess.CreateExpertGroupAccessPort;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.users.expertgroupaccess.ExpertGroupAccessJpaEntity;
import org.flickit.assessment.users.application.port.out.expertgroupaccess.InviteExpertGroupMemberPort;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExpertGroupAccessMapper {

    static ExpertGroupAccessJpaEntity mapCreateParamToJpaEntity(CreateExpertGroupAccessPort.Param param) {
        return new ExpertGroupAccessJpaEntity(
            null,
            param.expertGroupId(),
            null,
            null,
            param.userId(),
            null,
            param.status().ordinal()
        );
    }

    static ExpertGroupAccessJpaEntity mapInviteParamToJpaEntity(InviteExpertGroupMemberPort.Param param) {
        return new ExpertGroupAccessJpaEntity(
            null,
            param.expertGroupId(),
            param.inviteDate(),
            param.inviteExpirationDate(),
            param.userId(),
            param.inviteToken(),
            param.status().ordinal()
        );
    }
}
