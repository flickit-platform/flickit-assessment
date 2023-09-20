package org.flickit.flickitassessmentcore.application.service.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.assessment.CheckComparativeAssessmentsUseCase;
import org.flickit.flickitassessmentcore.application.port.out.assessment.LoadAssessmentsPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CheckComparativeAssessmentsService implements CheckComparativeAssessmentsUseCase {

    private final LoadAssessmentsPort loadAssessmentsPort;

    @Override
    public List<AssessmentListItem> checkComparativeAssessments(Param param) {
        var assessmentListItems = loadAssessmentsPort.load(param.getAssessmentIds());
        // TODO: progress
        // TODO: check Kit
        return null;
    }
}
