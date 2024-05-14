package org.flickit.assessment.common.permission;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AssessmentPermission {

    VIEW_ANSWER("view-answer"),
    ANSWER_QUESTION("answer-question"),
    VIEW_REPORT_ASSESSMENT("view-report-assessment"),
    CALCULATE_ASSESSMENT("calculate-assessment"),
    CALCULATE_CONFIDENCE("calculate-confidence"),
    CREATE_ASSESSMENT("create-assessment"),
    DELETE_ASSESSMENT("delete-assessment"),
    VIEW_ASSESSMENT_LIST("view-assessment-list"),
    VIEW_ASSESSMENT_PROGRESS("view-assessment-progress"),
    VIEW_ASSESSMENT("view-assessment"),
    UPDATE_ASSESSMENT("update-assessment"),
    VIEW_ATTRIBUTE_SCORE_DETAIL("view-attribute-score-detail"),
    ADD_EVIDENCE("add-evidence"),
    DELETE_EVIDENCE("delete-evidence"),
    VIEW_EVIDENCE("view-evidence"),
    VIEW_ATTRIBUTE_EVIDENCE_LIST("view-attribute-evidence-list"),
    VIEW_EVIDENCE_LIST("view-evidence-list"),
    UPDATE_EVIDENCE("update-evidence"),
    VIEW_ASSESSMENT_QUESTIONNAIRE_LIST("view-assessment-questionnaire-list"),
    VIEW_QUESTIONNAIRES_PROGRESS("view-questionnaires-progress"),
    VIEW_QUESTIONNAIRE_QUESTIONS("view-questionnaire-questions"),
    VIEW_SUBJECT_PROGRESS("view-subject-progress"),
    VIEW_SUBJECT_REPORT("view-subject-report"),
    CREATE_ADVICE("create-advice"),
    GRANT_USER_ASSESSMENT_ROLE("grant-user-assessment-role");

    private final String code;
}
