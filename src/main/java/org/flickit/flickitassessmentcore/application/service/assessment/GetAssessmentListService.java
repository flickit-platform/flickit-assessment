package org.flickit.flickitassessmentcore.application.service.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.domain.crud.AssessmentListItem;
import org.flickit.flickitassessmentcore.application.domain.crud.PaginatedResponse;
import org.flickit.flickitassessmentcore.application.port.in.assessment.GetAssessmentListUseCase;
import org.flickit.flickitassessmentcore.application.port.out.assessment.LoadAssessmentListItemsBySpacePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetAssessmentListService implements GetAssessmentListUseCase {

    private final LoadAssessmentListItemsBySpacePort loadAssessmentsBySpace;

    @Override
    public PaginatedResponse<AssessmentListItem> getAssessmentList(GetAssessmentListUseCase.Param param) {
        return loadAssessmentsBySpace.loadNotDeletedAssessments(
            param.getSpaceIds(),
            param.getKitId(),
            param.getPage(),
            param.getSize()
        );
    }
}
