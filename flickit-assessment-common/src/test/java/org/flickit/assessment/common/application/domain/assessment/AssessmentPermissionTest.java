package org.flickit.assessment.common.application.domain.assessment;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Map;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AssessmentPermissionTest {

    @Test
    void testAssessmentPermission_PermissionCodesShouldNotBeChanged() {
        Map<AssessmentPermission, String> permissionCodesMap = Map.ofEntries(
            Map.entry(CREATE_ASSESSMENT, "createAssessment"),
            Map.entry(VIEW_ASSESSMENT, "viewAssessment"),
            Map.entry(UPDATE_ASSESSMENT, "updateAssessment"),
            Map.entry(DELETE_ASSESSMENT, "deleteAssessment"),
            Map.entry(VIEW_ASSESSMENT_LIST, "viewAssessmentList"),
            Map.entry(VIEW_ASSESSMENT_PROGRESS, "viewAssessmentProgress"),
            Map.entry(VIEW_ASSESSMENT_REPORT, "viewAssessmentReport"),
            Map.entry(CALCULATE_ASSESSMENT, "calculateAssessment"),
            Map.entry(CALCULATE_CONFIDENCE, "calculateConfidence"),
            Map.entry(VIEW_ATTRIBUTE_SCORE_DETAIL, "viewAttributeScoreDetail"),
            Map.entry(VIEW_SUBJECT_PROGRESS, "viewSubjectProgress"),
            Map.entry(VIEW_SUBJECT_REPORT, "viewSubjectReport"),
            Map.entry(CREATE_ADVICE, "createAdvice"),
            Map.entry(ADD_EVIDENCE, "addEvidence"),
            Map.entry(UPDATE_EVIDENCE, "updateEvidence"),
            Map.entry(DELETE_EVIDENCE, "deleteEvidence"),
            Map.entry(VIEW_EVIDENCE_LIST, "viewEvidenceList"),
            Map.entry(VIEW_ATTRIBUTE_EVIDENCE_LIST, "viewAttributeEvidenceList"),
            Map.entry(ADD_EVIDENCE_ATTACHMENT, "addEvidenceAttachment"),
            Map.entry(VIEW_EVIDENCE_ATTACHMENT, "viewEvidenceAttachment"),
            Map.entry(DELETE_EVIDENCE_ATTACHMENT, "deleteEvidenceAttachment"),
            Map.entry(ANSWER_QUESTION, "answerQuestion"),
            Map.entry(VIEW_QUESTIONNAIRE_QUESTIONS, "viewQuestionnaireQuestions"),
            Map.entry(VIEW_ASSESSMENT_QUESTIONNAIRE_LIST, "viewAssessmentQuestionnaireList"),
            Map.entry(GRANT_USER_ASSESSMENT_ROLE, "grantUserAssessmentRole"),
            Map.entry(UPDATE_USER_ASSESSMENT_ROLE, "updateUserAssessmentRole"),
            Map.entry(DELETE_USER_ASSESSMENT_ROLE, "deleteUserAssessmentRole"),
            Map.entry(VIEW_ASSESSMENT_USER_LIST, "viewAssessmentUserList"),
            Map.entry(VIEW_ASSESSMENT_INVITEE_LIST, "viewAssessmentInviteeList"),
            Map.entry(VIEW_EVIDENCE, "viewEvidence"),
            Map.entry(VIEW_ATTRIBUTE_VALUE_EXCEL, "viewAttributeValueExcel"));

        permissionCodesMap.forEach((key, value) -> assertEquals(value, key.getCode()));
        Arrays.stream(AssessmentPermission.values()).forEach(e ->
            assertTrue(permissionCodesMap.containsValue(e.getCode()))
        );
        assertEquals(31, AssessmentPermission.values().length);
    }
}
