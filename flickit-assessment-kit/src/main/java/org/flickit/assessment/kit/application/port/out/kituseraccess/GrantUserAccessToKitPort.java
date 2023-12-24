package org.flickit.assessment.kit.application.port.out.kituseraccess;

public interface GrantUserAccessToKitPort {

    void grantUserAccess(Long kitId, String email);
}
