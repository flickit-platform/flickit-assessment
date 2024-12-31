package org.flickit.assessment.core.application.domain;

import org.flickit.assessment.common.application.domain.assessment.AssessmentPermission;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.ANSWER_QUESTION;
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_ASSESSMENT;
import static org.flickit.assessment.core.application.domain.AssessmentUserRole.*;
import static org.junit.jupiter.api.Assertions.*;

class AssessmentUserRoleTest {

    @Test
    void testAssessmentUserRole_IdOfAssessmentUserRoleShouldNotBeChanged() {
        assertEquals(0, VIEWER.getId());
        assertEquals(1, COMMENTER.getId());
        assertEquals(2, ASSESSOR.getId());
        assertEquals(3, MANAGER.getId());
        assertEquals(4, ASSOCIATE.getId());
        assertEquals(5, REPORT_VIEWER.getId());

        assertEquals(6, AssessmentUserRole.values().length);
    }

    @Test
    void testAssessmentUserRole_TitleOfRolesShouldNotBeChanged() {
        assertEquals("Viewer", VIEWER.getTitle());
        assertEquals("Commenter", COMMENTER.getTitle());
        assertEquals("Assessor", ASSESSOR.getTitle());
        assertEquals("Manager", MANAGER.getTitle());
        assertEquals("Associate", ASSOCIATE.getTitle());
        assertEquals("ReportViewer", REPORT_VIEWER.getTitle());
    }

    @Test
    void testAssessmentUserRole_ReportViewerPermissionsShouldBeSubsetOfCommenterAssessorManagerAssociateAndViewerReportViewer() {
        assertTrue(COMMENTER.getPermissions().containsAll(REPORT_VIEWER.getPermissions()));
        assertTrue(ASSESSOR.getPermissions().containsAll(REPORT_VIEWER.getPermissions()));
        assertTrue(MANAGER.getPermissions().containsAll(REPORT_VIEWER.getPermissions()));
        assertTrue(VIEWER.getPermissions().containsAll(REPORT_VIEWER.getPermissions()));
        assertTrue(ASSOCIATE.getPermissions().containsAll(REPORT_VIEWER.getPermissions()));
    }

    @Test
    void testAssessmentUserRole_ViewerPermissionsShouldBeSubsetOfCommenterAssessorAndManager() {
        assertTrue(COMMENTER.getPermissions().containsAll(VIEWER.getPermissions()));
        assertTrue(ASSESSOR.getPermissions().containsAll(VIEWER.getPermissions()));
        assertTrue(MANAGER.getPermissions().containsAll(VIEWER.getPermissions()));
    }

    @Test
    void testAssessmentUserRole_CommenterPermissionsShouldBeSubsetOfAssessorAndManager() {
        assertTrue(ASSESSOR.getPermissions().containsAll(COMMENTER.getPermissions()));
        assertTrue(MANAGER.getPermissions().containsAll(COMMENTER.getPermissions()));
    }

    @Test
    void testAssessmentUserRole_AssessorPermissionsShouldBeSubsetOfManager() {
        assertTrue(MANAGER.getPermissions().containsAll(ASSESSOR.getPermissions()));
    }

    @Test
    void testAssessmentUserRole_ManagerShouldHaveAllPermissions() {
        assertTrue(MANAGER.getPermissions().containsAll(List.of(AssessmentPermission.values())));
    }

    @Test
    void testAssessmentUserRole_AssociateShouldNotHaveViewerAllPermissions() {
        assertFalse(ASSOCIATE.getPermissions().containsAll(VIEWER.getPermissions()));
    }

    @Test
    void testAssessmentUserRole_AssociateShouldNotHaveAssessorAllPermissions() {
        assertTrue(ASSESSOR.getPermissions().containsAll(ASSOCIATE.getPermissions()));
        assertFalse(ASSOCIATE.getPermissions().containsAll(ASSESSOR.getPermissions()));
    }

    @Test
    void testAssessmentUserRole_AssociateShouldHaveAnswerQuestionPermission() {
        assertTrue(ASSOCIATE.hasAccess(ANSWER_QUESTION));
    }

    @Test
    void testAssessmentUserRole_PermissionListShouldBeUnmodifiable() {
        Set<AssessmentPermission> viewerPermissions = VIEWER.getPermissions();
        Set<AssessmentPermission> commenterPermissions = COMMENTER.getPermissions();
        Set<AssessmentPermission> assessorPermissions = ASSESSOR.getPermissions();
        Set<AssessmentPermission> managerPermissions = MANAGER.getPermissions();
        assertThrows(UnsupportedOperationException.class, () -> viewerPermissions.remove(VIEW_ASSESSMENT));
        assertThrows(UnsupportedOperationException.class, () -> viewerPermissions.add(VIEW_ASSESSMENT));
        assertThrows(UnsupportedOperationException.class, () -> commenterPermissions.remove(VIEW_ASSESSMENT));
        assertThrows(UnsupportedOperationException.class, () -> commenterPermissions.add(VIEW_ASSESSMENT));
        assertThrows(UnsupportedOperationException.class, () -> assessorPermissions.remove(VIEW_ASSESSMENT));
        assertThrows(UnsupportedOperationException.class, () -> assessorPermissions.add(VIEW_ASSESSMENT));
        assertThrows(UnsupportedOperationException.class, () -> managerPermissions.remove(VIEW_ASSESSMENT));
        assertThrows(UnsupportedOperationException.class, () -> managerPermissions.add(VIEW_ASSESSMENT));
    }
}
