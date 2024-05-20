package org.flickit.assessment.core.application.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.assessment.AssessmentPermission;

import java.util.Set;

import static org.flickit.assessment.common.application.assessment.AssessmentPermission.*;

@Getter
@RequiredArgsConstructor
public enum PermissionGroup {

    VIEWER_PERMISSIONS(Set.of(
        VIEW_REPORT_ASSESSMENT,
        CALCULATE_ASSESSMENT,
        CALCULATE_CONFIDENCE,
        VIEW_ASSESSMENT_LIST,
        VIEW_ASSESSMENT_PROGRESS,
        VIEW_ASSESSMENT,
        VIEW_SUBJECT_PROGRESS,
        VIEW_SUBJECT_REPORT)),
    COMMENTER_PERMISSIONS(Set.of(
        VIEW_ANSWER,
        ADD_EVIDENCE,
        DELETE_EVIDENCE,
        VIEW_EVIDENCE,
        VIEW_ATTRIBUTE_EVIDENCE_LIST,
        VIEW_EVIDENCE_LIST,
        UPDATE_EVIDENCE,
        VIEW_ASSESSMENT_QUESTIONNAIRE_LIST,
        VIEW_QUESTIONNAIRES_PROGRESS,
        VIEW_QUESTIONNAIRE_QUESTIONS)),
    ASSESSOR_PERMISSIONS(Set.of(
        ANSWER_QUESTION,
        VIEW_ATTRIBUTE_SCORE_DETAIL,
        CREATE_ADVICE)),
    MANAGER_PERMISSIONS(Set.of(
        CREATE_ASSESSMENT,
        DELETE_ASSESSMENT,
        UPDATE_ASSESSMENT));

    private final Set<AssessmentPermission> permissions;
}
