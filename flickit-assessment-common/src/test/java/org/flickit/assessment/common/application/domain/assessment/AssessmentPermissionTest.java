package org.flickit.assessment.common.application.domain.assessment;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AssessmentPermissionTest {

    private static final int ASSESSMENT_PERMISSION_SIZE = 28;
    private static final List<String> ASSESSMENT_PERMISSION_CODES = List.of(
        "createAssessment", "viewAssessment", "updateAssessment", "deleteAssessment",
        "viewAssessmentList", "viewAssessmentProgress","viewAssessmentReport", "calculateAssessment",
        "calculateConfidence", "viewAttributeScoreDetail", "viewSubjectProgress", "viewSubjectReport",
        "createAdvice", "addEvidence", "updateEvidence", "deleteEvidence",
        "viewEvidenceList", "viewAttributeEvidenceList", "addEvidenceAttachment", "viewEvidenceAttachment",
        "answerQuestion", "viewQuestionnaireQuestions", "viewAssessmentQuestionnaireList", "grantUserAssessmentRole",
        "updateUserAssessmentRole", "deleteUserAssessmentRole", "viewAssessmentUserList", "viewAssessmentInviteeList");

    @Test
    void testAssessmentPermissionsLength() {
        assertEquals(ASSESSMENT_PERMISSION_SIZE, AssessmentPermission.values().length);
    }

    @ParameterizedTest
    @EnumSource(AssessmentPermission.class)
    void testAssessmentPermissionCodes(AssessmentPermission assessmentPermission) {
        assertTrue(ASSESSMENT_PERMISSION_CODES.contains(assessmentPermission.getCode()));
    }
}
