package org.flickit.assessment.kit.adapter.out.persistence.expertgroupaccess;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.kit.expertgroupaccess.ExpertGroupAccessJpaEntity;
import org.flickit.assessment.data.jpa.kit.expertgroupaccess.ExpertGroupAccessStatus;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CreateExpertGroupAccessPort;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.InviteExpertGroupMemberPort;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExpertGroupAccessMapper {

    static ExpertGroupAccessJpaEntity mapCreateParamToJpaEntity(CreateExpertGroupAccessPort.Param param) {
        return new ExpertGroupAccessJpaEntity(
            null,
            param.expertGroupId(),
            param.inviteExpirationDate(),
            param.userId(),
            ExpertGroupAccessStatus.ACTIVE
        );
    }

    static ExpertGroupAccessJpaEntity mapInviteParamToJpaEntity(InviteExpertGroupMemberPort.Param param) {
        return new ExpertGroupAccessJpaEntity(
            null,
            param.expertGroupId(),
            null,
            param.userId(),
            param.status()
        );
    }
}
