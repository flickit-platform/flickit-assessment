package org.flickit.assessment.core.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.permission.AssessmentPermission;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import static org.flickit.assessment.common.permission.AssessmentPermission.*;

@Getter
@RequiredArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum AssessmentUserRole {

    VIEWER("viewer", Collections.unmodifiableSet(RolePermissionSet.VIEWER_PERMISSIONS)),
    COMMENTER("commenter", Collections.unmodifiableSet(RolePermissionSet.COMMENTER_PERMISSIONS)),
    ASSESSOR("assessor", Collections.unmodifiableSet(RolePermissionSet.ASSESSOR_PERMISSIONS)),
    MANAGER("manager", Collections.unmodifiableSet(RolePermissionSet.MANAGER_PERMISSIONS));

    private final String title;
    private final Set<AssessmentPermission> permissions;

    public int getId() {
        return this.ordinal();
    }

    public static AssessmentUserRole valueOfById(int id) {
        return Stream.of(AssessmentUserRole.values())
            .filter(x -> x.getId() == id)
            .findAny()
            .orElse(null);
    }

    public static boolean isValidId(int id) {
        return id >= 0 && id < AssessmentUserRole.values().length;
    }

    public boolean hasAccess(AssessmentPermission permission) {
        return this.getPermissions().contains(permission);
    }

    private static class RolePermissionSet {

        private static final Set<AssessmentPermission> VIEWER_PERMISSIONS = new HashSet<>();
        private static final Set<AssessmentPermission> COMMENTER_PERMISSIONS = new HashSet<>();
        private static final Set<AssessmentPermission> ASSESSOR_PERMISSIONS = new HashSet<>();
        private static final Set<AssessmentPermission> MANAGER_PERMISSIONS = new HashSet<>();

        static {
            VIEWER_PERMISSIONS.addAll(Set.of(
                VIEW_REPORT_ASSESSMENT,
                CALCULATE_ASSESSMENT,
                CALCULATE_CONFIDENCE,
                VIEW_ASSESSMENT_LIST,
                VIEW_ASSESSMENT_PROGRESS,
                VIEW_ASSESSMENT,
                VIEW_SUBJECT_PROGRESS,
                VIEW_SUBJECT_REPORT
            ));
            COMMENTER_PERMISSIONS.addAll(VIEWER_PERMISSIONS);
            COMMENTER_PERMISSIONS.addAll(Set.of(
                VIEW_ANSWER,
                ADD_EVIDENCE,
                DELETE_EVIDENCE,
                VIEW_EVIDENCE,
                VIEW_ATTRIBUTE_EVIDENCE_LIST,
                VIEW_EVIDENCE_LIST,
                UPDATE_EVIDENCE,
                VIEW_ASSESSMENT_QUESTIONNAIRE_LIST,
                VIEW_QUESTIONNAIRES_PROGRESS,
                VIEW_QUESTIONNAIRE_QUESTIONS
            ));
            ASSESSOR_PERMISSIONS.addAll(COMMENTER_PERMISSIONS);
            ASSESSOR_PERMISSIONS.addAll(Set.of(
                ANSWER_QUESTION,
                VIEW_ATTRIBUTE_SCORE_DETAIL,
                CREATE_ADVICE
            ));
            MANAGER_PERMISSIONS.addAll(ASSESSOR_PERMISSIONS);
            MANAGER_PERMISSIONS.addAll(Set.of(
                CREATE_ASSESSMENT,
                DELETE_ASSESSMENT,
                UPDATE_ASSESSMENT,
                GRANT_USER_ASSESSMENT_ROLE,
                DELETE_USER_ASSESSMENT_ROLE
            ));
        }
    }
}
