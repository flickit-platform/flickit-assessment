package org.flickit.assessment.kit.application.port.out.useraccess;

public interface GrantUserAccessToKitPort {

    boolean grantUserAccessToKitByUserEmail(Long kitId, String userEmail);
}
