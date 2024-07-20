package org.flickit.assessment.common.application.domain.assessment;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AssessmentPermission {

    ANSWER_QUESTION("answerQuestion"),
    VIEW_REPORT_ASSESSMENT("viewReportAssessment"),
    CALCULATE_ASSESSMENT("calculateAssessment"),
    CALCULATE_CONFIDENCE("calculateConfidence"),
    CREATE_ASSESSMENT("createAssessment"),
    DELETE_ASSESSMENT("deleteAssessment"),
    VIEW_ASSESSMENT_LIST("viewAssessmentList"),
    VIEW_ASSESSMENT_PROGRESS("viewAssessmentProgress"),
    VIEW_ASSESSMENT("viewAssessment"),
    UPDATE_ASSESSMENT("updateAssessment"),
    VIEW_ATTRIBUTE_SCORE_DETAIL("viewAttributeScoreDetail"),
    ADD_EVIDENCE("addEvidence"),
    DELETE_EVIDENCE("deleteEvidence"),
    VIEW_ATTRIBUTE_EVIDENCE_LIST("viewAttributeEvidenceList"),
    VIEW_EVIDENCE_LIST("viewEvidenceList"),
    VIEW_EVIDENCE_ATTACHMENT("viewEvidenceAttachment"),
    UPDATE_EVIDENCE("updateEvidence"),
    ADD_EVIDENCE_ATTACHMENT("addEvidenceAttachment"),
    VIEW_ASSESSMENT_QUESTIONNAIRE_LIST("viewAssessmentQuestionnaireList"),
    VIEW_QUESTIONNAIRE_QUESTIONS("viewQuestionnaireQuestions"),
    VIEW_SUBJECT_PROGRESS("viewSubjectProgress"),
    VIEW_SUBJECT_REPORT("viewSubjectReport"),
    CREATE_ADVICE("createAdvice"),
    GRANT_USER_ASSESSMENT_ROLE("grantUserAssessmentRole"),
    UPDATE_USER_ASSESSMENT_ROLE("updateUserAssessmentRole"),
    DELETE_USER_ASSESSMENT_ROLE("deleteUserAssessmentRole"),
    VIEW_ASSESSMENT_USER_LIST("viewAssessmentUserList"),
    VIEW_ASSESSMENT_INVITEE_LIST("viewAssessmentInviteeList");

    private final String code;
}
