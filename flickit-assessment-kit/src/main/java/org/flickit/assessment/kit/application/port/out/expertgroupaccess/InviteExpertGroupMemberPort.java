package org.flickit.assessment.kit.application.port.out.expertgroupaccess;

import org.flickit.assessment.data.jpa.kit.expertgroupaccess.ExpertGroupAccessStatus;

import java.util.UUID;

public interface InviteExpertGroupMemberPort {

    void invite(Param param);

    record Param(long expertGroupId,
                 UUID userId,
                 UUID currentUserId,
                 ExpertGroupAccessStatus status) {
    }
}
