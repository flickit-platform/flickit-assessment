package org.flickit.assessment.users.application.port.out.space;

import java.time.LocalDateTime;
import java.util.UUID;

public interface AddSpaceMemberPort {

    void addMemberAccess(long spaceId, UUID invitee, UUID inviter, LocalDateTime inviteTime);
}
