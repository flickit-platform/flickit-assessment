package org.flickit.assessment.core.application.service.assessmentuserrole;

import org.flickit.assessment.core.application.port.in.assessmentuserrole.GetAssessmentUserRolesUseCase.AssessmentUserRoleItem;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;

class GetAssessmentUserRolesServiceTest {

    private final GetAssessmentUserRolesService service = new GetAssessmentUserRolesService();

    @Test
    void testGetAssessmentUserRoles_resultShouldBeOrderedByIdAsc() {
        var expectedIds = List.of(0, 1, 2, 3);

        var roles = service.getAssessmentUserRoles();
        var ids = roles.stream().map(AssessmentUserRoleItem::id)
            .toList();

        assertIterableEquals(expectedIds, ids);
    }
}
