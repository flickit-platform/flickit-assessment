package org.flickit.assessment.kit.adapter.out.persistence.expertgroupaccess;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.kit.expertgroupaccess.ExpertGroupAccessJpaEntity;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CreateExpertGroupAccessPort.Param;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExpertGroupAccessMapper {

    static ExpertGroupAccessJpaEntity mapCreateParamToJpaEntity(Param param) {
        return new ExpertGroupAccessJpaEntity(
            null,
            param.expertGroupId(),
            param.inviteEmail(),
            param.inviteExpirationDate(),
            param.userId()
        );
    }
}
