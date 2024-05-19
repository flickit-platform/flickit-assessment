package org.flickit.assessment.users.application.port.out.spaceuseraccess;

import java.util.UUID;

public interface DeleteSpaceMemberPort {

    void delete(long spaceId, UUID userId);
}
