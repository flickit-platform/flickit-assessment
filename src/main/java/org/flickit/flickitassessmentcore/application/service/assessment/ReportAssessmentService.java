package org.flickit.flickitassessmentcore.application.service.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.domain.Assessment;
import org.flickit.flickitassessmentcore.application.domain.AssessmentResult;
import org.flickit.flickitassessmentcore.application.domain.MaturityLevel;
import org.flickit.flickitassessmentcore.application.domain.QualityAttributeValue;
import org.flickit.flickitassessmentcore.application.domain.report.AssessmentReport;
import org.flickit.flickitassessmentcore.application.domain.report.AssessmentReport.AssessmentReportItem;
import org.flickit.flickitassessmentcore.application.domain.report.AssessmentReport.AttributeReportItem;
import org.flickit.flickitassessmentcore.application.domain.report.AssessmentReport.SubjectReportItem;
import org.flickit.flickitassessmentcore.application.port.in.assessment.ReportAssessmentUseCase;
import org.flickit.flickitassessmentcore.application.port.out.assessmentresult.LoadAssessmentReportInfoPort;
import org.flickit.flickitassessmentcore.application.port.out.qualityattributevalue.LoadAttributeValueListPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static java.util.Collections.reverseOrder;
import static java.util.Comparator.comparing;
import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.toMap;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReportAssessmentService implements ReportAssessmentUseCase {

    private final LoadAssessmentReportInfoPort loadReportInfoPort;
    private final LoadAttributeValueListPort loadAttributeValueListPort;


    private static final int TOP_COUNT = 3;

    @Override
    public AssessmentReport reportAssessment(Param param) {
        var assessmentResult = loadReportInfoPort.load(param.getAssessmentId());

        var maturityLevels = assessmentResult.getAssessment().getAssessmentKit().getMaturityLevels();
        Map<Long, MaturityLevel> maturityLevelsMap = maturityLevels.stream()
            .collect(toMap(MaturityLevel::getId, x -> x));

        var attributeValues = loadAttributeValueListPort.loadAttributeValues(assessmentResult.getId(), maturityLevelsMap);

        var assessmentReportItem = buildAssessment(assessmentResult);
        var subjectReportItems = buildSubjects(assessmentResult);
        var midLevelMaturity = middleLevel(maturityLevels);
        var topStrengths = getTopStrengths(attributeValues, midLevelMaturity);
        var topWeaknesses = getTopWeaknesses(attributeValues, midLevelMaturity);

        return new AssessmentReport(
            assessmentReportItem,
            topStrengths,
            topWeaknesses,
            subjectReportItems);
    }

    private AssessmentReportItem buildAssessment(AssessmentResult assessmentResult) {
        Assessment assessment = assessmentResult.getAssessment();
        return new AssessmentReport.AssessmentReportItem(
            assessment.getId(),
            assessment.getTitle(),
            assessmentResult.getMaturityLevel().getId(),
            assessmentResult.isValid(),
            assessment.getColorId(),
            assessment.getLastModificationTime()
        );
    }

    private List<SubjectReportItem> buildSubjects(AssessmentResult assessmentResult) {
        return assessmentResult.getSubjectValues()
            .stream()
            .map(x -> new SubjectReportItem(x.getSubject().getId(), x.getMaturityLevel().getId()))
            .toList();
    }

    private List<AttributeReportItem> getTopStrengths(List<QualityAttributeValue> attributeValues, MaturityLevel midLevelMaturity) {
        return attributeValues.stream()
            .sorted(comparing(x -> x.getMaturityLevel().getLevel(), reverseOrder()))
            .filter(x -> isHigherThanOrEqualToMiddleLevel(x.getMaturityLevel(), midLevelMaturity))
            .limit(TOP_COUNT)
            .map(x -> new AttributeReportItem(x.getQualityAttribute().getId()))
            .toList();
    }

    private boolean isHigherThanOrEqualToMiddleLevel(MaturityLevel maturityLevel, MaturityLevel midLevelMaturity) {
        return maturityLevel.getLevel() >= midLevelMaturity.getLevel();
    }

    private List<AttributeReportItem> getTopWeaknesses(List<QualityAttributeValue> attributeValues, MaturityLevel midLevelMaturity) {
        return attributeValues.stream()
            .sorted(comparingInt(x -> x.getMaturityLevel().getLevel()))
            .filter(x -> isLowerThanMiddleLevel(x.getMaturityLevel(), midLevelMaturity))
            .limit(TOP_COUNT)
            .map(x -> new AttributeReportItem(x.getQualityAttribute().getId()))
            .toList();
    }

    private boolean isLowerThanMiddleLevel(MaturityLevel maturityLevel, MaturityLevel midLevelMaturity) {
        return maturityLevel.getLevel() < midLevelMaturity.getLevel();
    }

    private MaturityLevel middleLevel(List<MaturityLevel> maturityLevels) {
        var sortedMaturityLevels = maturityLevels.stream()
            .sorted(comparingInt(MaturityLevel::getLevel))
            .toList();
        return sortedMaturityLevels.get((sortedMaturityLevels.size() / 2));
    }

}
