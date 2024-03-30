package org.flickit.assessment.users.application.port.out.expertgroupaccess;

import java.util.UUID;

public interface DeleteExpertGroupMemberPort {

    void deleteMember (UUID userId, long expertGroupId);
}
