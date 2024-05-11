package org.flickit.assessment.core.common;

import org.junit.jupiter.api.Test;

import static org.flickit.assessment.core.common.PermissionConstants.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PermissionConstantsTest {

    @Test
    void testPermissionConstants_ViewerPermissionsShouldBeSubsetOfCommenterAssessorAndManager() {
        assertTrue(getCommenterPermission().containsAll(getViewerPermission()));
        assertTrue(getAssessorPermission().containsAll(getViewerPermission()));
        assertTrue(getManagerPermission().containsAll(getViewerPermission()));
    }

    @Test
    void testPermissionConstants_CommenterPermissionsShouldBeSubsetOfAssessorAndManager() {
        assertTrue(getAssessorPermission().containsAll(getCommenterPermission()));
        assertTrue(getManagerPermission().containsAll(getCommenterPermission()));
    }

    @Test
    void testPermissionConstants_AssessorPermissionsShouldBeSubsetOfManager() {
        assertTrue(getManagerPermission().containsAll(getAssessorPermission()));
    }
}
