package org.flickit.assessment.users.application.port.out.expertgroupaccess;

import org.flickit.assessment.users.application.domain.ExpertGroupAccessStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public interface InviteExpertGroupMemberPort {

    boolean persist(Param param);

    record Param(long expertGroupId,
                 UUID userId,
                 UUID currentUserId,
                 LocalDateTime inviteDate,
                 LocalDateTime inviteExpirationDate,
                 UUID inviteToken,
                 ExpertGroupAccessStatus status) {
    }
}
