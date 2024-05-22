package org.flickit.assessment.core.application.service.assessmentuserrole;

import org.flickit.assessment.core.application.domain.AssessmentUserRole;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GetAssessmentUserRolesServiceTest {

    private final GetAssessmentUserRolesService service = new GetAssessmentUserRolesService();

    @Test
    void testGetAssessmentUserRoles_resultShouldBeOrderedByIdAsc() {
        var roles = service.getAssessmentUserRoles();
        var roleValues = AssessmentUserRole.values();

        assertEquals(roleValues.length, roles.size());

        assertEquals(roleValues[0].getId(), roles.get(0).id());
        assertEquals(roleValues[0].getTitle(), roles.get(0).title());
        assertEquals(roleValues[1].getId(), roles.get(1).id());
        assertEquals(roleValues[1].getTitle(), roles.get(1).title());
        assertEquals(roleValues[2].getId(), roles.get(2).id());
        assertEquals(roleValues[2].getTitle(), roles.get(2).title());
        assertEquals(roleValues[3].getId(), roles.get(3).id());
        assertEquals(roleValues[3].getTitle(), roles.get(3).title());
    }
}
