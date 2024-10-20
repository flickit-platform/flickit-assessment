package org.flickit.assessment.users.application.port.out.expertgroupaccess;

import java.util.Optional;
import java.util.UUID;

public interface LoadExpertGroupMemberStatusPort {

    Optional<Integer> getMemberStatus(long expertGroupId, UUID userId);
}
