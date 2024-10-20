package org.flickit.assessment.core.application.port.out.assessmentuserrole;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;

import java.util.UUID;

public interface LoadAssessmentUsersPort {

    PaginatedResponse<AssessmentUser> loadAssessmentUsers(Param param);

    record Param(UUID assessmentId, int size, int page) {}

    record AssessmentUser(
        UUID id,
        String email,
        String displayName,
        String picturePath,
        Role role,
        boolean editable) {

        public record Role(int id, String title) {
        }
    }
}
