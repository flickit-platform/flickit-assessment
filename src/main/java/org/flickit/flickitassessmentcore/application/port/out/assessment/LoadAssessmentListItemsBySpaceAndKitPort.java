package org.flickit.flickitassessmentcore.application.port.out.assessment;

import org.flickit.flickitassessmentcore.application.domain.crud.PaginatedResponse;
import org.flickit.flickitassessmentcore.application.port.in.assessment.GetComparableAssessmentsUseCase;

import java.util.List;

public interface LoadAssessmentListItemsBySpaceAndKitPort {

    PaginatedResponse<GetComparableAssessmentsUseCase.AssessmentListItem> loadBySpaceIdAndKitId(
        List<Long> spaceIds, Long kitId, int page, int size
    );

}
