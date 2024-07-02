package org.flickit.assessment.core.application.service.assessmentuserrole;

import org.flickit.assessment.core.application.domain.AssessmentUserRole;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GetAssessmentUserRolesServiceTest {

    private final GetAssessmentUserRolesService service = new GetAssessmentUserRolesService();

    @Test
    void testGetAssessmentUserRoles_resultShouldBeOrderedByIdAsc() {
        var roles = service.getAssessmentUserRoles();
        var roleValues = Arrays.stream(AssessmentUserRole.values())
            .sorted(Comparator.comparing(AssessmentUserRole::getIndex)).toList();

        assertEquals(roleValues.size(), roles.size());

        assertEquals(roleValues.get(0).getId(), roles.get(0).id());
        assertEquals(roleValues.get(0).getTitle(), roles.get(0).title());
        assertEquals(roleValues.get(1).getId(), roles.get(1).id());
        assertEquals(roleValues.get(1).getTitle(), roles.get(1).title());
        assertEquals(roleValues.get(2).getId(), roles.get(2).id());
        assertEquals(roleValues.get(2).getTitle(), roles.get(2).title());
        assertEquals(roleValues.get(3).getId(), roles.get(3).id());
        assertEquals(roleValues.get(3).getTitle(), roles.get(3).title());
    }
}
