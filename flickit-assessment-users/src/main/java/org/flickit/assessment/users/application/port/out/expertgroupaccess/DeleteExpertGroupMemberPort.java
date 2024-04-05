package org.flickit.assessment.users.application.port.out.expertgroupaccess;

import java.util.UUID;

public interface DeleteExpertGroupMemberPort {

    void deleteMember (long expertGroupId, UUID userId);
}
