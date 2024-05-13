package org.flickit.assessment.core.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

import static org.flickit.assessment.core.common.AssessmentPermission.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PermissionConstants {

    static Set<AssessmentPermission> getViewerPermission() {
        return Set.of(
            VIEW_REPORT_ASSESSMENT,
            CALCULATE_ASSESSMENT,
            CALCULATE_CONFIDENCE,
            VIEW_ASSESSMENT_LIST,
            VIEW_ASSESSMENT_PROGRESS,
            VIEW_ASSESSMENT,
            VIEW_SUBJECT_PROGRESS,
            VIEW_SUBJECT_REPORT);
    }

    static Set<AssessmentPermission> getCommenterPermission() {
        Set<AssessmentPermission> permissions = new HashSet<>(Set.of(
            VIEW_ANSWER,
            ADD_EVIDENCE,
            DELETE_EVIDENCE,
            VIEW_EVIDENCE,
            VIEW_ATTRIBUTE_EVIDENCE_LIST,
            VIEW_EVIDENCE_LIST,
            UPDATE_EVIDENCE,
            VIEW_ASSESSMENT_QUESTIONNAIRE_LIST,
            VIEW_QUESTIONNAIRES_PROGRESS,
            VIEW_QUESTIONNAIRE_QUESTIONS));
        permissions.addAll(getViewerPermission());
        return permissions;
    }

    static Set<AssessmentPermission> getAssessorPermission() {
        Set<AssessmentPermission> permissions = new HashSet<>(Set.of(
            ANSWER_QUESTION,
            VIEW_ATTRIBUTE_SCORE_DETAIL,
            CREATE_ADVICE));
        permissions.addAll(getCommenterPermission());
        return permissions;
    }

    static Set<AssessmentPermission> getManagerPermission() {
        Set<AssessmentPermission> permissions = new HashSet<>(Set.of(
            CREATE_ASSESSMENT,
            DELETE_ASSESSMENT,
            UPDATE_ASSESSMENT,
            GRANT_USER_ASSESSMENT_ROLE));
        permissions.addAll(getAssessorPermission());
        return permissions;
    }
}
