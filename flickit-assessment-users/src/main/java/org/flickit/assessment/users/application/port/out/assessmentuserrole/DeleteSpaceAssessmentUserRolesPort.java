package org.flickit.assessment.users.application.port.out.assessmentuserrole;

import java.util.UUID;

public interface DeleteSpaceAssessmentUserRolesPort {

    void delete(long spaceId, UUID userId);
}
