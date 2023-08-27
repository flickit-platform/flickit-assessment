package org.flickit.flickitassessmentcore.application.service.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.assessment.GetAssessmentListUseCase;
import org.flickit.flickitassessmentcore.application.port.out.assessment.LoadAssessmentListItemsBySpacePort;
import org.flickit.flickitassessmentcore.domain.crud.PaginatedResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetAssessmentListService implements GetAssessmentListUseCase {

    private final LoadAssessmentListItemsBySpacePort loadAssessmentsBySpace;

    @Override
    public PaginatedResponse<AssessmentListItem> getAssessmentList(GetAssessmentListUseCase.Param param) {
        return loadAssessmentsBySpace.loadAssessments(
            param.getSpaceId(),
            param.getPage(),
            param.getSize()
        );
    }
}
