package org.flickit.assessment.core.adapter.in.rest.userassessmentrole;

import org.flickit.assessment.core.application.port.in.userassessmentrole.GetAssessmentUserRolesUseCase.AssessmentUserRoleItem;

import java.util.List;

public record GetAssessmentUserRolesResponseDto(List<AssessmentUserRoleItem> items) {
}
