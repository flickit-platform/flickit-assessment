package org.flickit.assessment.core.application.port.out.assessmentinvitee;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.core.application.domain.AssessmentInvitee;

import java.util.UUID;

public interface LoadAssessmentInviteeListPort {

    PaginatedResponse<AssessmentInvitee> loadByAssessmentId(UUID assessmentId, int size, int page);
}
