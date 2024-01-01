package org.flickit.assessment.kit.application.port.out.assessmentkit;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetAssessmentKitListUseCase;

public interface LoadAssessmentKitListPort {

    PaginatedResponse<GetAssessmentKitListUseCase.AssessmentKitListItem> load();
}
