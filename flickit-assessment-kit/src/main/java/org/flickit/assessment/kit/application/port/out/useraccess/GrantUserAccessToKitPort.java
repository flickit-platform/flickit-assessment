package org.flickit.assessment.kit.application.port.out.useraccess;

public interface GrantUserAccessToKitPort {

    void grantUserAccess(Long kitId, String email);
}
