package org.flickit.flickitassessmentcore.application.service.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.domain.*;
import org.flickit.flickitassessmentcore.application.domain.report.SubjectReport;
import org.flickit.flickitassessmentcore.application.domain.report.TopAttribute;
import org.flickit.flickitassessmentcore.application.domain.report.TopAttributeResolver;
import org.flickit.flickitassessmentcore.application.port.in.assessment.CompareAssessmentsUseCase;
import org.flickit.flickitassessmentcore.application.port.out.assessment.GetAssessmentProgressPort;
import org.flickit.flickitassessmentcore.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.flickitassessmentcore.application.port.out.maturitylevel.LoadMaturityLevelsByKitPort;
import org.flickit.flickitassessmentcore.application.port.out.qualityattributevalue.LoadAttributeValueListPort;
import org.flickit.flickitassessmentcore.application.port.out.subject.LoadSubjectReportInfoPort;
import org.flickit.flickitassessmentcore.application.port.out.subjectvalue.LoadSubjectsPort;
import org.flickit.flickitassessmentcore.application.service.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.util.stream.Collectors.toMap;
import static org.flickit.flickitassessmentcore.application.domain.MaturityLevel.middleLevel;
import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.COMPARE_ASSESSMENTS_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.REPORT_SUBJECT_ASSESSMENT_SUBJECT_VALUE_NOT_FOUND;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CompareAssessmentsService implements CompareAssessmentsUseCase {

    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final LoadMaturityLevelsByKitPort loadMaturityLevelsByKitPort;
    private final LoadAttributeValueListPort loadAttributeValueListPort;
    private final GetAssessmentProgressPort getAssessmentProgressPort;
    private final LoadSubjectsPort loadSubjectsPort;
    private final LoadSubjectReportInfoPort loadSubjectReportInfoPort;

    @Override
    public List<CompareListItem> compareAssessments(Param param) {
        var items = new ArrayList<CompareListItem>();
        for (UUID assessmentId : param.getAssessmentIds()) {
            var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(assessmentId)
                .orElseThrow(() -> new ResourceNotFoundException(COMPARE_ASSESSMENTS_ASSESSMENT_RESULT_NOT_FOUND));

            var topAttributeResolver = getTopAttributeResolver(assessmentResult);
            var topStrengths = topAttributeResolver.getTopStrengths();
            var topWeaknesses = topAttributeResolver.getTopWeaknesses();

            var assessmentProgress = getAssessmentProgressPort.getAssessmentProgressById(assessmentId);

            List<Long> subjectIds =  loadSubjectsPort.loadSubjectIdsByAssessmentId(assessmentId);
            List<SubjectReport> subjectsReport = new ArrayList<>();
            for (Long subjectId : subjectIds) {
                SubjectReport subjectReport = buildSubjectReport(assessmentId, subjectId);
                subjectsReport.add(subjectReport);
            }

            CompareListItem item = createResult(assessmentResult, topStrengths, topWeaknesses, assessmentProgress.allAnswersCount(), subjectsReport);
            items.add(item);
        }

        return items;
    }

    private SubjectReport buildSubjectReport(UUID assessmentId, Long subjectId) {
        AssessmentResult assessmentRes = loadSubjectReportInfoPort.load(assessmentId, subjectId);
        var maturityLevels = assessmentRes.getAssessment().getAssessmentKit().getMaturityLevels();

        var subjectValue = assessmentRes.getSubjectValues()
            .stream()
            .filter(s -> s.getSubject().getId() == subjectId)
            .findAny()
            .orElseThrow(() -> new ResourceNotFoundException(REPORT_SUBJECT_ASSESSMENT_SUBJECT_VALUE_NOT_FOUND));

        var attributeValues = subjectValue.getQualityAttributeValues();

        var subjectReportItem = buildSubject(subjectValue, assessmentRes.isValid());
        var attributeReportItems = buildAttributes(attributeValues);

        var midLevelMaturity = middleLevel(maturityLevels);
        TopAttributeResolver topAttributeResolver = new TopAttributeResolver(attributeValues, midLevelMaturity);
        var topStrengths = topAttributeResolver.getTopStrengths();
        var topWeaknesses = topAttributeResolver.getTopWeaknesses();

        return new SubjectReport(
            subjectReportItem,
            topStrengths,
            topWeaknesses,
            attributeReportItems);
    }

    private SubjectReport.SubjectReportItem buildSubject(SubjectValue subjectValue, boolean isCalculateValid) {
        return new SubjectReport.SubjectReportItem(
            subjectValue.getSubject().getId(),
            subjectValue.getMaturityLevel().getId(),
            isCalculateValid
        );
    }

    private List<SubjectReport.AttributeReportItem> buildAttributes(List<QualityAttributeValue> attributeValues) {
        return attributeValues.stream()
            .map(x -> new SubjectReport.AttributeReportItem(x.getQualityAttribute().getId(), x.getMaturityLevel().getId()))
            .toList();
    }

    private TopAttributeResolver getTopAttributeResolver(AssessmentResult assessmentResult) {
        var maturityLevels = loadMaturityLevelsByKitPort.loadByKitId(assessmentResult.getAssessment().getAssessmentKit().getId());
        Map<Long, MaturityLevel> maturityLevelsMap = maturityLevels.stream()
            .collect(toMap(MaturityLevel::getId, x -> x));

        var attributeValues = loadAttributeValueListPort.loadAttributeValues(assessmentResult.getId(), maturityLevelsMap);

        var midLevelMaturity = middleLevel(maturityLevels);
        return new TopAttributeResolver(attributeValues, midLevelMaturity);
    }

    private CompareListItem createResult(AssessmentResult assessmentResult, List<TopAttribute> topStrengths, List<TopAttribute> topWeaknesses, int answersCount, List<SubjectReport> subjectsReport) {
        Assessment assessment = assessmentResult.getAssessment();
        return new CompareListItem(
            assessment.getId(),
            assessment.getTitle(),
            assessment.getAssessmentKit().getId(),
            assessment.getSpaceId(),
            AssessmentColor.valueOfById(assessment.getColorId()),
            assessmentResult.getMaturityLevel().getId(),
            answersCount,
            topStrengths,
            topWeaknesses,
            subjectsReport
        );
    }
}
