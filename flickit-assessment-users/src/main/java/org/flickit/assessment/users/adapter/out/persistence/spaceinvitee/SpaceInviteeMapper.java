package org.flickit.assessment.users.adapter.out.persistence.spaceinvitee;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.users.spaceinvitee.SpaceInviteeJpaEntity;
import org.flickit.assessment.users.application.domain.SpaceInvitee;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.InviteSpaceMemberPort;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SpaceInviteeMapper {

    static SpaceInviteeJpaEntity mapCreateParamToJpaEntity(InviteSpaceMemberPort.Param param) {
        return new SpaceInviteeJpaEntity(
            null,
            param.spaceId(),
            param.email(),
            param.createdBy(),
            param.creationTime(),
            param.expirationDate()
        );
    }

    static SpaceInvitee mapToDomain(SpaceInviteeJpaEntity entity) {
        return new SpaceInvitee(
            entity.getId(),
            entity.getEmail(),
            entity.getSpaceId(),
            entity.getCreatedBy(),
            entity.getCreationTime(),
            entity.getExpirationDate());
    }
}
