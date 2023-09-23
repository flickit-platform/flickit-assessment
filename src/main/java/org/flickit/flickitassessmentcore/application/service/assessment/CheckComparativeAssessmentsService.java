package org.flickit.flickitassessmentcore.application.service.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.assessment.CheckComparativeAssessmentsUseCase;
import org.flickit.flickitassessmentcore.application.port.out.assessment.LoadAssessmentsPort;
import org.flickit.flickitassessmentcore.application.service.exception.AssessmentsNotComparableException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.CHECK_COMPARATIVE_ASSESSMENTS_ASSESSMENTS_NOT_COMPARABLE;

@Service
@Transactional
@RequiredArgsConstructor
public class CheckComparativeAssessmentsService implements CheckComparativeAssessmentsUseCase {

    private final LoadAssessmentsPort loadAssessmentsPort;

    @Override
    public List<AssessmentListItem> checkComparativeAssessments(Param param) {
        var assessmentListItems = loadAssessmentsPort.load(param.getAssessmentIds());
        checkAssessmentsKits(assessmentListItems);
        return assessmentListItems;
    }

    private void checkAssessmentsKits(List<AssessmentListItem> assessmentListItems) {
        var uniqueKitIds = assessmentListItems.stream()
            .map(AssessmentListItem::assessmentKitId)
            .collect(Collectors.toSet());
        if (uniqueKitIds.size() > 1) {
            throw new AssessmentsNotComparableException(CHECK_COMPARATIVE_ASSESSMENTS_ASSESSMENTS_NOT_COMPARABLE);
        }
    }
}
