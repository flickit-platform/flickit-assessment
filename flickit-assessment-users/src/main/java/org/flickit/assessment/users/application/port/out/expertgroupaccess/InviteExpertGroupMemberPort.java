package org.flickit.assessment.users.application.port.out.expertgroupaccess;

import org.flickit.assessment.users.application.domain.ExpertGroupAccessStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public interface InviteExpertGroupMemberPort {

    void invite(Param param);

    record Param(long expertGroupId,
                 UUID userId,
                 LocalDateTime inviteDate,
                 LocalDateTime inviteExpirationDate,
                 UUID inviteToken,
                 ExpertGroupAccessStatus status,
                 UUID createdBy) {
    }
}
