package org.flickit.assessment.core.adapter.out.persistence.spaceinvitee;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.core.application.port.out.space.CreateSpaceInvitePort;
import org.flickit.assessment.data.jpa.users.spaceinvitee.SpaceInviteeJpaEntity;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SpaceInviteeMapper {

    public static SpaceInviteeJpaEntity mapToJpaEntity(UUID id, CreateSpaceInvitePort.Param param) {
        return new SpaceInviteeJpaEntity(id,
            param.spaceId(),
            param.email(),
            param.createdBy(),
            param.creationTime(),
            param.expirationDate());
    }
}
