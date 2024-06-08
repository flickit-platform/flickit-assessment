package org.flickit.assessment.core.application.service.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.core.application.domain.AssessmentListItem;
import org.flickit.assessment.core.application.port.in.assessment.GetAssessmentListUseCase;
import org.flickit.assessment.core.application.port.out.assessment.LoadAssessmentListPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetAssessmentListService implements GetAssessmentListUseCase {

    private final LoadAssessmentListPort loadAssessmentListPort;

    @Override
    public PaginatedResponse<AssessmentListItem> getAssessmentList(Param param) {
        return loadAssessmentListPort.loadUserAssessments(
            param.getKitId(),
            param.getCurrentUserId(),
            param.getPage(),
            param.getSize()
        );
    }
}
