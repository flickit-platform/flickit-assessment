package org.flickit.assessment.core.application.port.out.assessmentuserrole;

import org.flickit.assessment.core.application.domain.AssessmentUserRoleItem;

import java.util.List;

public interface GrantUserAssessmentRolePort {

    void persist(AssessmentUserRoleItem assessmentUserRoleItem);

    void persistAll(List<AssessmentUserRoleItem> assessmentUserRoleItems);
}
