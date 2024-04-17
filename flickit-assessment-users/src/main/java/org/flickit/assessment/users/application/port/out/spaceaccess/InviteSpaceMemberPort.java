package org.flickit.assessment.users.application.port.out.spaceaccess;

import java.time.LocalDateTime;
import java.util.UUID;

public interface InviteSpaceMemberPort {

    void inviteMember(Param param);

    record Param(long spaceId, String inviteeMail, UUID inviterId, LocalDateTime inviteDate) {
    }
}
