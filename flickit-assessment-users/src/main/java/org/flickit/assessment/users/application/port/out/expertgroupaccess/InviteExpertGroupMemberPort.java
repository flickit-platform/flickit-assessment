package org.flickit.assessment.users.application.port.out.expertgroupaccess;

import org.flickit.assessment.data.jpa.users.expertgroupaccess.ExpertGroupAccessStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public interface InviteExpertGroupMemberPort {

    void persist(Param param);

    record Param(long expertGroupId,
                 UUID userId,
                 UUID currentUserId,
                 LocalDateTime inviteDate,
                 LocalDateTime inviteExpirationDate,
                 UUID inviteToken,
                 ExpertGroupAccessStatus status) {
    }
}
