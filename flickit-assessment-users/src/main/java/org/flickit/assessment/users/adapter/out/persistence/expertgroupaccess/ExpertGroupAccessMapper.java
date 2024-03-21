package org.flickit.assessment.users.adapter.out.persistence.expertgroupaccess;

import org.flickit.assessment.users.application.port.out.expertgroupaccess.CreateExpertGroupAccessPort;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.users.expertgroupaccess.ExpertGroupAccessJpaEntity;
import org.flickit.assessment.users.application.port.out.expertgroupaccess.InviteExpertGroupMemberPort;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExpertGroupAccessMapper {

    static ExpertGroupAccessJpaEntity mapCreateParamToJpaEntity(CreateExpertGroupAccessPort.Param param) {
        LocalDateTime creationTime = LocalDateTime.now();
        return new ExpertGroupAccessJpaEntity(
            param.expertGroupId(),
            param.userId(),
            null,
            null,
            null,
            param.status().ordinal(),
            param.userId(),
            param.userId(),
            creationTime,
            creationTime
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
