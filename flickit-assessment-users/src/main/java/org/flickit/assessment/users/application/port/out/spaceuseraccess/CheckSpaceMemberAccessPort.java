package org.flickit.assessment.users.application.port.out.spaceuseraccess;

import java.util.UUID;
public interface CheckSpaceMemberAccessPort {

    boolean checkIsMember(UUID userId);
}
