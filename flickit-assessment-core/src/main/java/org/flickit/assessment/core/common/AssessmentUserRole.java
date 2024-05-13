package org.flickit.assessment.core.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

import static org.flickit.assessment.core.common.PermissionConstants.*;

@Getter
@RequiredArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum AssessmentUserRole {

    VIEWER("viewer"),
    COMMENTER("commenter"),
    ASSESSOR("assessor"),
    MANAGER("manager");

    private final String title;

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
        return switch (this) {
            case VIEWER -> getViewerPermission().contains(permission);
            case COMMENTER -> getCommenterPermission().contains(permission);
            case ASSESSOR -> getAssessorPermission().contains(permission);
            case MANAGER -> getManagerPermission().contains(permission);
        };
    }
}
