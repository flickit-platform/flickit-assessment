package org.flickit.flickitassessmentcore.application.service.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.assessment.GetAssessmentListUseCase;
import org.flickit.flickitassessmentcore.application.port.out.assessment.LoadAssessmentBySpacePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class GetAssessmentListService implements GetAssessmentListUseCase {

    private final LoadAssessmentBySpacePort loadAssessmentBySpace;

    @Override
    public GetAssessmentListUseCase.Result viewListOfSpaceAssessments(GetAssessmentListUseCase.Param param) {
        return new GetAssessmentListUseCase.Result(loadAssessmentBySpace.loadAssessmentBySpaceId(param.getSpaceId()));
    }
}
