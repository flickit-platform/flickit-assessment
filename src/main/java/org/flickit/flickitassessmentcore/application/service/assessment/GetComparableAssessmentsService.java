package org.flickit.flickitassessmentcore.application.service.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.domain.crud.PaginatedResponse;
import org.flickit.flickitassessmentcore.application.port.in.assessment.GetComparableAssessmentsUseCase;
import org.flickit.flickitassessmentcore.application.port.out.assessment.LoadAssessmentListItemsBySpaceAndKitPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetComparableAssessmentsService implements GetComparableAssessmentsUseCase {

    private final LoadAssessmentListItemsBySpaceAndKitPort loadAssessmentListItemsBySpaceAndKitPort;

    @Override
    public PaginatedResponse<AssessmentListItem> getComparableAssessments(Param param) {
        return loadAssessmentListItemsBySpaceAndKitPort.loadBySpaceIdAndKitId(
            param.getSpaceIds(), param.getKitId(), param.getPage(), param.getSize()
        );
    }
}
