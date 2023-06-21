package org.flickit.flickitassessmentcore.application.service.assessment;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.assessment.ViewListOfSpaceAssessmentsCommand;
import org.flickit.flickitassessmentcore.application.port.in.assessment.ViewListOfSpaceAssessmentsUseCase;
import org.flickit.flickitassessmentcore.application.port.out.assessment.LoadAssessmentBySpacePort;
import org.flickit.flickitassessmentcore.domain.Assessment;
import org.springframework.stereotype.Service;

import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
public class ViewListOfSpaceAssessmentsService implements ViewListOfSpaceAssessmentsUseCase {

    private final LoadAssessmentBySpacePort loadAssessmentBySpace;

    @Override
    public List<Assessment> viewListOfSpaceAssessments(ViewListOfSpaceAssessmentsCommand command) {
        return loadAssessmentBySpace.loadAssessmentBySpaceId(command.getSpaceId());
    }
}
