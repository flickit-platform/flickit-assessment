package org.flickit.assessment.core.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AssessmentUserRoleTest {

    @Test
    void testAssessmentUserRole_IdOfAssessmentUserRoleShouldNotBeChanged() {
        assertEquals(0, AssessmentUserRole.VIEWER.getId());
        assertEquals(1, AssessmentUserRole.COMMENTER.getId());
        assertEquals(2, AssessmentUserRole.ASSESSOR.getId());
        assertEquals(3, AssessmentUserRole.MANAGER.getId());

        assertEquals(4, AssessmentUserRole.values().length);
    }
}
