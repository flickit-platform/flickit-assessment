package org.flickit.assessment.users.adapter.out.persistence.expertgroupaccess;

import org.flickit.assessment.users.application.port.out.expertgroupaccess.CreateExpertGroupAccessPort;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.users.expertgroupaccess.ExpertGroupAccessJpaEntity;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExpertGroupAccessMapper {

    static ExpertGroupAccessJpaEntity mapCreateParamToJpaEntity(CreateExpertGroupAccessPort.Param param) {
        LocalDateTime creationTime = LocalDateTime.now();
        return new ExpertGroupAccessJpaEntity(
            param.expertGroupId(),
            param.userId(),
            param.inviteEmail(),
            param.inviteExpirationDate(),
            param.userId(),
            param.userId(),
            creationTime,
            creationTime,
            Boolean.FALSE
        );
    }
}
