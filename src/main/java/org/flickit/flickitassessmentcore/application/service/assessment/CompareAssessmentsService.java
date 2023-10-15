package org.flickit.flickitassessmentcore.application.service.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.domain.AssessmentColor;
import org.flickit.flickitassessmentcore.application.domain.AssessmentResult;
import org.flickit.flickitassessmentcore.application.domain.MaturityLevel;
import org.flickit.flickitassessmentcore.application.domain.crud.AssessmentListItem;
import org.flickit.flickitassessmentcore.application.domain.report.TopAttribute;
import org.flickit.flickitassessmentcore.application.domain.report.TopAttributeResolver;
import org.flickit.flickitassessmentcore.application.port.in.assessment.CompareAssessmentsUseCase;
import org.flickit.flickitassessmentcore.application.port.out.assessment.GetAssessmentProgressPort;
import org.flickit.flickitassessmentcore.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.flickitassessmentcore.application.port.out.maturitylevel.LoadMaturityLevelsByKitPort;
import org.flickit.flickitassessmentcore.application.port.out.qualityattributevalue.LoadAttributeValueListPort;
import org.flickit.flickitassessmentcore.application.service.exception.AssessmentsNotComparableException;
import org.flickit.flickitassessmentcore.application.service.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;
import static org.flickit.flickitassessmentcore.application.domain.MaturityLevel.middleLevel;
import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.COMPARE_ASSESSMENTS_ASSESSMENTS_NOT_COMPARABLE;
import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.COMPARE_ASSESSMENTS_ASSESSMENT_RESULT_NOT_FOUND;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CompareAssessmentsService implements CompareAssessmentsUseCase {

    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final LoadMaturityLevelsByKitPort loadMaturityLevelsByKitPort;
    private final LoadAttributeValueListPort loadAttributeValueListPort;
    private final GetAssessmentProgressPort getAssessmentProgressPort;

    @Override
    public List<CompareListItem> compareAssessments(Param param) {
        var items = new ArrayList<CompareListItem>();
        var assessmentResults = new ArrayList<AssessmentResult>();
        for (UUID assessmentId : param.getAssessmentIds()) {
            var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(assessmentId)
                .orElseThrow(() -> new ResourceNotFoundException(COMPARE_ASSESSMENTS_ASSESSMENT_RESULT_NOT_FOUND));
            assessmentResults.add(assessmentResult);
        }
        Long kitId = checkAssessmentsKits(assessmentResults);
        var maturityLevels = loadMaturityLevelsByKitPort.loadByKitId(kitId);

        for (AssessmentResult assessmentResult : assessmentResults) {
            var topAttributeResolver = getTopAttributeResolver(assessmentResult, maturityLevels);
            var topStrengths = topAttributeResolver.getTopStrengths();
            var topWeaknesses = topAttributeResolver.getTopWeaknesses();

            var assessmentId = assessmentResult.getAssessment().getId();
            var assessmentProgress = getAssessmentProgressPort.getAssessmentProgressById(assessmentId);

            var item = mapToResult(assessmentResult, topStrengths, topWeaknesses, assessmentProgress.allAnswersCount());
            items.add(item);
        }

        return items;
    }

    private Long checkAssessmentsKits(List<AssessmentResult> comparableAssessmentListItems) {
        var uniqueKitIds = comparableAssessmentListItems.stream()
            .map(a -> a.getAssessment().getAssessmentKit().getId())
            .collect(Collectors.toSet());
        if (uniqueKitIds.size() > 1) {
            throw new AssessmentsNotComparableException(COMPARE_ASSESSMENTS_ASSESSMENTS_NOT_COMPARABLE);
        }
        return (Long) uniqueKitIds.toArray()[0];
    }

    private TopAttributeResolver getTopAttributeResolver(AssessmentResult assessmentResult, List<MaturityLevel> maturityLevels) {
        Map<Long, MaturityLevel> maturityLevelsMap = maturityLevels.stream()
            .collect(toMap(MaturityLevel::getId, x -> x));

        var attributeValues = loadAttributeValueListPort.loadAttributeValues(assessmentResult.getId(), maturityLevelsMap);

        var midLevelMaturity = middleLevel(maturityLevels);
        return new TopAttributeResolver(attributeValues, midLevelMaturity);
    }

    private CompareListItem mapToResult(AssessmentResult assessmentResult, List<TopAttribute> topStrengths, List<TopAttribute> topWeaknesses, int answersCount) {
        var assessment = assessmentResult.getAssessment();
        return new CompareListItem(
            new AssessmentListItem(
                assessment.getId(),
                assessment.getTitle(),
                assessment.getAssessmentKit().getId(),
                assessment.getSpaceId(),
                AssessmentColor.valueOfById(assessment.getColorId()),
                assessment.getLastModificationTime(),
                assessmentResult.getMaturityLevel().getId(),
                assessmentResult.isValid()),
            answersCount,
            topStrengths,
            topWeaknesses
        );
    }
}
