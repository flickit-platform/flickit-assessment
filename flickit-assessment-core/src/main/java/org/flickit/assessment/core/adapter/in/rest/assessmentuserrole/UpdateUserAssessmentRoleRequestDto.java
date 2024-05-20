package org.flickit.assessment.core.adapter.in.rest.assessmentuserrole;

import java.util.UUID;

public record UpdateUserAssessmentRoleRequestDto(UUID userId, Integer roleId) {
}
