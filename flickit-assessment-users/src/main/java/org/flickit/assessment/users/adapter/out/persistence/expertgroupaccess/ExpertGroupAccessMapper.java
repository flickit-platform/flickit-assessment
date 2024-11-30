package org.flickit.assessment.users.adapter.out.persistence.expertgroupaccess;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.users.expertgroupaccess.ExpertGroupAccessJpaEntity;
import org.flickit.assessment.users.application.domain.ExpertGroupAccess;
import org.flickit.assessment.users.application.domain.ExpertGroupAccessStatus;
import org.flickit.assessment.users.application.port.out.expertgroupaccess.CreateExpertGroupAccessPort;
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
            creationTime,
            creationTime
        );
    }

    static ExpertGroupAccessJpaEntity mapInviteParamToJpaEntity(InviteExpertGroupMemberPort.Param param) {
        LocalDateTime creationTime = LocalDateTime.now();
        return new ExpertGroupAccessJpaEntity(
            param.expertGroupId(),
            param.userId(),
            param.inviteDate(),
            param.inviteExpirationDate(),
            param.inviteToken(),
            param.status().ordinal(),
            param.createdBy(),
            param.createdBy(),
            creationTime,
            creationTime,
            creationTime
        );
    }

    static ExpertGroupAccess mapAccessJpaToExpertGroupAccessModel(ExpertGroupAccessJpaEntity entity) {
        return new ExpertGroupAccess(
            entity.getInviteExpirationDate(),
            entity.getInviteToken(),
            ExpertGroupAccessStatus.valueOfById(entity.getStatus()));
    }
}
