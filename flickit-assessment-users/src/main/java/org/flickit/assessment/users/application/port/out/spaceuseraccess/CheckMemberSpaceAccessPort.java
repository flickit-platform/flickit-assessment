package org.flickit.assessment.users.application.port.out.spaceuseraccess;

import java.util.UUID;

public interface CheckMemberSpaceAccessPort {

    boolean checkIsMember(long spaceId, UUID userId);
}
