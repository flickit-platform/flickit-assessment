package org.flickit.assessment.users.application.port.out.assessmentuserrole;

import java.util.UUID;

public interface DeleteAssessmentUserRoleByUserIdPort {

    void delete(UUID userId);
}
