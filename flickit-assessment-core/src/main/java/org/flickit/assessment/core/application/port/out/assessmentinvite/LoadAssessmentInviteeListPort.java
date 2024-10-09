package org.flickit.assessment.core.application.port.out.assessmentinvite;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.core.application.domain.AssessmentInvite;

import java.util.UUID;

public interface LoadAssessmentInviteeListPort {

    PaginatedResponse<AssessmentInvite> loadByAssessmentId(UUID assessmentId, int size, int page);
}
