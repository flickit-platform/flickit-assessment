package org.flickit.assessment.core.application.port.in.userassessmentrole;

import java.util.List;

public interface GetAssessmentUserRolesUseCase {

    List<AssessmentUserRoleItem> getAssessmentUserRoles();

    record AssessmentUserRoleItem(int id, String title) {
    }
}
