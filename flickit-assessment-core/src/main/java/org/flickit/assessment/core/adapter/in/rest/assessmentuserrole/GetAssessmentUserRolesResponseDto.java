package org.flickit.assessment.core.adapter.in.rest.assessmentuserrole;

import org.flickit.assessment.core.application.port.in.assessmentuserrole.GetAssessmentUserRolesUseCase.AssessmentUserRoleItem;

import java.util.List;

public record GetAssessmentUserRolesResponseDto(List<AssessmentUserRoleItem> items) {
}
