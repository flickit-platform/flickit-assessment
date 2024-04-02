package org.flickit.assessment.users.application.port.out.expertgroupaccess;

import org.flickit.assessment.users.application.domain.ExpertGroupAccess;

import java.util.UUID;

public interface LoadExpertGroupAccessPort {

    ExpertGroupAccess loadExpertGroupAccess(long expertGroupId, UUID userId);
}
