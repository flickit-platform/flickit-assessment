package org.flickit.assessment.users.adapter.out.persistence.spaceinvitee;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.users.spaceinvitee.SpaceInviteeJpaEntity;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.SaveSpaceMemberInviteePort;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SpaceInviteeMapper {

    static SpaceInviteeJpaEntity mapCreateParamToJpaEntity(SaveSpaceMemberInviteePort.Param param) {
        LocalDateTime creationTime = LocalDateTime.now();
        return new SpaceInviteeJpaEntity(
            null,
            param.spaceId(),
            param.inviteeMail(),
            creationTime,
            creationTime,
            param.inviterId()
        );
    }

}
