package org.flickit.assessment.core.application.service.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.domain.Assessment;
import org.flickit.assessment.core.application.domain.AssessmentColor;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.domain.MaturityLevel;
import org.flickit.assessment.core.application.domain.report.AssessmentReport;
import org.flickit.assessment.core.application.domain.report.AssessmentReport.AssessmentReportItem;
import org.flickit.assessment.core.application.domain.report.AssessmentReport.SubjectReportItem;
import org.flickit.assessment.core.application.domain.report.TopAttributeResolver;
import org.flickit.assessment.core.application.port.in.assessment.ReportAssessmentUseCase;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentReportInfoPort;
import org.flickit.assessment.core.application.port.out.qualityattributevalue.LoadAttributeValueListPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;
import static org.flickit.assessment.core.application.domain.MaturityLevel.middleLevel;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReportAssessmentService implements ReportAssessmentUseCase {

    private final LoadAssessmentReportInfoPort loadReportInfoPort;
    private final LoadAttributeValueListPort loadAttributeValueListPort;

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
        TopAttributeResolver topAttributeResolver = new TopAttributeResolver(attributeValues, midLevelMaturity);
        var topStrengths = topAttributeResolver.getTopStrengths();
        var topWeaknesses = topAttributeResolver.getTopWeaknesses();

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
            assessmentResult.getConfidenceValue(),
            assessmentResult.isCalculateValid(),
            assessmentResult.isConfidenceValid(),
            AssessmentColor.valueOfById(assessment.getColorId()),
            assessment.getLastModificationTime()
        );
    }

    private List<SubjectReportItem> buildSubjects(AssessmentResult assessmentResult) {
        return assessmentResult.getSubjectValues()
            .stream()
            .map(x -> new SubjectReportItem(x.getSubject().getId(), x.getMaturityLevel().getId()))
            .toList();
    }
}
