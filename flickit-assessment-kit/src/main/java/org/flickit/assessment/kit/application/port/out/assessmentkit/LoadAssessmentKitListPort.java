package org.flickit.assessment.kit.application.port.out.assessmentkit;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetAssessmentKitListUseCase.AssessmentKitListItem;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetAssessmentKitListUseCase.Param;

public interface LoadAssessmentKitListPort {

    PaginatedResponse<AssessmentKitListItem> loadKitList(Param param);
}
