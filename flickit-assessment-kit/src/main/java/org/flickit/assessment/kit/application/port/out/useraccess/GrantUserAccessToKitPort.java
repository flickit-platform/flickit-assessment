package org.flickit.assessment.kit.application.port.out.useraccess;

public interface GrantUserAccessToKitPort {

    boolean grantUserAccess(Long kitId, String email);
}
