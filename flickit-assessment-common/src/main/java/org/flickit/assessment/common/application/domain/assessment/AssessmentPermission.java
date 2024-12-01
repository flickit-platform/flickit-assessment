package org.flickit.assessment.common.application.domain.assessment;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AssessmentPermission {

    CREATE_ASSESSMENT("createAssessment"),
    VIEW_ASSESSMENT("viewAssessment"),
    UPDATE_ASSESSMENT("updateAssessment"),
    DELETE_ASSESSMENT("deleteAssessment"),
    VIEW_ASSESSMENT_LIST("viewAssessmentList"),
    VIEW_ASSESSMENT_PROGRESS("viewAssessmentProgress"),
    VIEW_ASSESSMENT_REPORT("viewAssessmentReport"),
    EXPORT_ASSESSMENT_REPORT("exportAssessmentReport"),
    CREATE_ATTRIBUTE_INSIGHT("createAttributeInsight"),
    CALCULATE_ASSESSMENT("calculateAssessment"),
    MIGRATE_KIT_VERSION("migrateKitVersion"),
    CALCULATE_CONFIDENCE("calculateConfidence"),
    VIEW_ATTRIBUTE_SCORE_DETAIL("viewAttributeScoreDetail"),
    VIEW_SUBJECT_PROGRESS("viewSubjectProgress"),
    VIEW_SUBJECT_REPORT("viewSubjectReport"),
    CREATE_ADVICE("createAdvice"),
    ADD_EVIDENCE("addEvidence"),
    UPDATE_EVIDENCE("updateEvidence"),
    DELETE_EVIDENCE("deleteEvidence"),
    VIEW_EVIDENCE_LIST("viewEvidenceList"),
    VIEW_ATTRIBUTE_EVIDENCE_LIST("viewAttributeEvidenceList"),
    ADD_EVIDENCE_ATTACHMENT("addEvidenceAttachment"),
    VIEW_EVIDENCE_ATTACHMENT("viewEvidenceAttachment"),
    DELETE_EVIDENCE_ATTACHMENT("deleteEvidenceAttachment"),
    ANSWER_QUESTION("answerQuestion"),
    VIEW_ANSWER_HISTORY_LIST("viewAnswerHistory"),
    VIEW_QUESTIONNAIRE_QUESTIONS("viewQuestionnaireQuestions"),
    VIEW_ASSESSMENT_QUESTIONNAIRE_LIST("viewAssessmentQuestionnaireList"),
    GRANT_USER_ASSESSMENT_ROLE("grantUserAssessmentRole"),
    UPDATE_USER_ASSESSMENT_ROLE("updateUserAssessmentRole"),
    DELETE_USER_ASSESSMENT_ROLE("deleteUserAssessmentRole"),
    VIEW_ASSESSMENT_USER_LIST("viewAssessmentUserList"),
    VIEW_ASSESSMENT_INVITEE_LIST("viewAssessmentInviteeList"),
    VIEW_EVIDENCE("viewEvidence"),
    DELETE_ASSESSMENT_INVITE("deleteAssessmentInvite"),
    CREATE_ASSESSMENT_INSIGHT("createAssessmentInsight"),
    CREATE_SUBJECT_INSIGHT("createSubjectInsight"),
    MANAGE_ADD_ON("manageAddOn");

    private final String code;
}
