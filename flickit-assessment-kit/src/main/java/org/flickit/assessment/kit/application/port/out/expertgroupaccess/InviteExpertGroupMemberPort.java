package org.flickit.assessment.kit.application.port.out.expertgroupaccess;

import java.time.LocalDate;
import java.util.UUID;

public interface InviteExpertGroupMemberPort {

    void invite(Param param);

    record Param(long expertGroupId,
                 UUID userId,
                 UUID currentUserId) {
    }
}
