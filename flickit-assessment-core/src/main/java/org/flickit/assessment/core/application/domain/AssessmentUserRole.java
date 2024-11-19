package org.flickit.assessment.core.application.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.application.domain.assessment.AssessmentPermission;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.*;
import static org.flickit.assessment.core.application.domain.AssessmentUserRole.PermissionGroup.*;

@Getter
@RequiredArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum AssessmentUserRole {

    VIEWER(1, "Viewer", VIEWER_PERMISSIONS),
    COMMENTER(2, "Commenter", VIEWER_PERMISSIONS, COMMENTER_PERMISSIONS),
    ASSESSOR(4, "Assessor", VIEWER_PERMISSIONS, COMMENTER_PERMISSIONS, ASSESSOR_PERMISSIONS),
    MANAGER(5, "Manager", VIEWER_PERMISSIONS, COMMENTER_PERMISSIONS, ASSESSOR_PERMISSIONS, MANAGER_PERMISSIONS),
    ASSOCIATE(3, "Associate", ASSOCIATE_PERMISSIONS);

    private final int index;
    private final String title;
    private final Set<AssessmentPermission> permissions;

    AssessmentUserRole(int index, String title, PermissionGroup... permissionsGroups) {
        this.index = index;
        this.title = title;
        this.permissions = Arrays.stream(permissionsGroups)
            .flatMap(x -> x.getPermissions().stream())
            .collect(Collectors.toUnmodifiableSet());
    }

    public int getId() {
        return this.ordinal();
    }

    public static AssessmentUserRole valueOfById(int id) {
        if (!isValidId(id))
            return null;
        return values()[id];
    }

    public static boolean isValidId(int id) {
        return id >= 0 && id < AssessmentUserRole.values().length;
    }

    public boolean hasAccess(AssessmentPermission permission) {
        return this.getPermissions().contains(permission);
    }

    public String getDescription() {
        return MessageBundle.message("AssessmentUserRole_" + name());
    }

    @Getter
    @RequiredArgsConstructor
    enum PermissionGroup {

        VIEWER_PERMISSIONS(Set.of(
            VIEW_ASSESSMENT_REPORT,
            CALCULATE_ASSESSMENT,
            MIGRATE_KIT_VERSION,
            CALCULATE_CONFIDENCE,
            VIEW_ASSESSMENT_LIST,
            VIEW_ASSESSMENT_PROGRESS,
            VIEW_ASSESSMENT,
            VIEW_SUBJECT_PROGRESS,
            VIEW_SUBJECT_REPORT,
            VIEW_ASSESSMENT_QUESTIONNAIRE_LIST,
            VIEW_EVIDENCE_ATTACHMENT,
            EXPORT_ASSESSMENT_REPORT)),
        COMMENTER_PERMISSIONS(Set.of(
            ADD_EVIDENCE,
            DELETE_EVIDENCE,
            VIEW_ATTRIBUTE_EVIDENCE_LIST,
            VIEW_EVIDENCE_LIST,
            UPDATE_EVIDENCE,
            ADD_EVIDENCE_ATTACHMENT,
            DELETE_EVIDENCE_ATTACHMENT,
            VIEW_QUESTIONNAIRE_QUESTIONS,
            VIEW_EVIDENCE)),
        ASSESSOR_PERMISSIONS(Set.of(
            ANSWER_QUESTION,
            VIEW_ATTRIBUTE_SCORE_DETAIL,
            CREATE_ADVICE,
            VIEW_ANSWER_HISTORY_LIST,
            CREATE_ATTRIBUTE_INSIGHT,
            CREATE_ASSESSMENT_INSIGHT,
            CREATE_SUBJECT_INSIGHT,
            MANAGE_ADD_ON,
            SET_KIT_CUSTOM)),
        MANAGER_PERMISSIONS(Set.of(
            CREATE_ASSESSMENT,
            DELETE_ASSESSMENT,
            UPDATE_ASSESSMENT,
            GRANT_USER_ASSESSMENT_ROLE,
            UPDATE_USER_ASSESSMENT_ROLE,
            DELETE_USER_ASSESSMENT_ROLE,
            VIEW_ASSESSMENT_USER_LIST,
            VIEW_ASSESSMENT_INVITEE_LIST,
            DELETE_ASSESSMENT_INVITE)),
        ASSOCIATE_PERMISSIONS(Set.of(
            VIEW_ASSESSMENT_LIST,
            VIEW_ASSESSMENT_PROGRESS,
            VIEW_ASSESSMENT,
            VIEW_SUBJECT_PROGRESS,
            ADD_EVIDENCE,
            DELETE_EVIDENCE,
            VIEW_EVIDENCE_LIST,
            UPDATE_EVIDENCE,
            VIEW_EVIDENCE_ATTACHMENT,
            ADD_EVIDENCE_ATTACHMENT,
            DELETE_EVIDENCE_ATTACHMENT,
            VIEW_ASSESSMENT_QUESTIONNAIRE_LIST,
            VIEW_QUESTIONNAIRE_QUESTIONS,
            ANSWER_QUESTION,
            VIEW_EVIDENCE));

        private final Set<AssessmentPermission> permissions;
    }
}
