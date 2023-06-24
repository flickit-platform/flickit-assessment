package org.flickit.flickitassessmentcore.application.service.assessment;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.assessment.ViewListOfSpaceAssessmentsUseCase;
import org.flickit.flickitassessmentcore.application.port.out.assessment.LoadAssessmentBySpacePort;
import org.springframework.stereotype.Service;

@Transactional
@Service
@RequiredArgsConstructor
public class ViewListOfSpaceAssessmentsService implements ViewListOfSpaceAssessmentsUseCase {

    private final LoadAssessmentBySpacePort loadAssessmentBySpace;

    @Override
    public ViewListOfSpaceAssessmentsUseCase.Result viewListOfSpaceAssessments(ViewListOfSpaceAssessmentsUseCase.Param param) {
        return new ViewListOfSpaceAssessmentsUseCase.Result(loadAssessmentBySpace.loadAssessmentBySpaceId(param.getSpaceId()));
    }
}
