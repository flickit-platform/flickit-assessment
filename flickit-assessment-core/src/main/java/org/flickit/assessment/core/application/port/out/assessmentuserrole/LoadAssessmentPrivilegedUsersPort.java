package org.flickit.assessment.core.application.port.out.assessmentuserrole;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;

import java.util.UUID;

public interface LoadAssessmentPrivilegedUsersPort {

    PaginatedResponse<AssessmentPrivilegedUser> loadAssessmentPrivilegedUsers(Param param);

    record Param(UUID assessmentId, int size, int page) {}

    record AssessmentPrivilegedUser(
        UUID id,
        String email,
        String displayName,
        String bio,
        String picturePath,
        String linkedin,
        Role role
    ) {

        public record Role(int id, String title) {}
    }
}
