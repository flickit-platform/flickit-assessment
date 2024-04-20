package org.flickit.assessment.users.application.port.out.spaceuseraccess;

import java.time.LocalDateTime;
import java.util.UUID;

public interface SaveSpaceMemberInviteePort {

    void persist(Param param);

    record Param(long spaceId, String inviteeMail, UUID inviterId,
                 LocalDateTime inviteDate, LocalDateTime inviteExpirationDate) {
    }
}
