package org.flickit.assessment.core.application.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.assessment.AssessmentPermission;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.flickit.assessment.core.application.domain.PermissionGroup.*;

@Getter
@RequiredArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum AssessmentUserRole {

    VIEWER("viewer", VIEWER_PERMISSIONS),
    COMMENTER("commenter", VIEWER_PERMISSIONS, COMMENTER_PERMISSIONS),
    ASSESSOR("assessor", VIEWER_PERMISSIONS, COMMENTER_PERMISSIONS, ASSESSOR_PERMISSIONS),
    MANAGER("manager", VIEWER_PERMISSIONS, COMMENTER_PERMISSIONS, ASSESSOR_PERMISSIONS, MANAGER_PERMISSIONS);

    private final String title;
    private final Set<AssessmentPermission> permissions;

    AssessmentUserRole(String title, PermissionGroup... permissionsGroups) {
        this.title = title;
        this.permissions = Arrays.stream(permissionsGroups)
            .flatMap(x -> x.getPermissions().stream())
            .collect(Collectors.toUnmodifiableSet());
    }

    public int getId() {
        return this.ordinal();
    }
}
