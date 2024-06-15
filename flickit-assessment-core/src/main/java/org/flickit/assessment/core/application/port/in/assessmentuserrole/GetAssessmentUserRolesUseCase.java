package org.flickit.assessment.core.application.port.in.assessmentuserrole;

import java.util.List;

public interface GetAssessmentUserRolesUseCase {

    List<AssessmentUserRoleItem> getAssessmentUserRoles();

    record AssessmentUserRoleItem(int id, String title, String description) {
    }
}
