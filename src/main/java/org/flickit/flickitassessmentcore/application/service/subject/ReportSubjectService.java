package org.flickit.flickitassessmentcore.application.service.subject;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.flickitassessmentcore.application.domain.QualityAttributeValue;
import org.flickit.flickitassessmentcore.application.domain.SubjectValue;
import org.flickit.flickitassessmentcore.application.domain.report.SubjectReport;
import org.flickit.flickitassessmentcore.application.domain.report.SubjectReport.AttributeReportItem;
import org.flickit.flickitassessmentcore.application.port.in.subject.ReportSubjectUseCase;
import org.flickit.flickitassessmentcore.application.port.out.subject.LoadSubjectReportInfoPort;
import org.flickit.flickitassessmentcore.application.service.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.REPORT_SUBJECT_ASSESSMENT_SUBJECT_VALUE_NOT_FOUND;
import static org.flickit.flickitassessmentcore.common.report.EntityReportCommonCalculations.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ReportSubjectService implements ReportSubjectUseCase {

    private final LoadSubjectReportInfoPort loadSubjectReportInfoPort;

    @Override
    public SubjectReport reportSubject(Param param) {
        var assessmentResult = loadSubjectReportInfoPort.load(param.getAssessmentId(), param.getSubjectId());

        var maturityLevels = assessmentResult.getAssessment().getAssessmentKit().getMaturityLevels();

        var subjectValue = assessmentResult.getSubjectValues()
            .stream()
            .filter(s -> s.getSubject().getId() == param.getSubjectId())
            .findAny()
            .orElseThrow(() -> new ResourceNotFoundException(REPORT_SUBJECT_ASSESSMENT_SUBJECT_VALUE_NOT_FOUND));


        var attributeValues = subjectValue.getQualityAttributeValues();

        var subjectReportItem = buildSubject(subjectValue, assessmentResult.isValid());
        var attributeReportItems = buildAttributes(attributeValues);
        var midLevelMaturity = middleLevel(maturityLevels);
        var topStrengths = toTopAttributeItem(getTopStrengths(attributeValues, midLevelMaturity));
        var topWeaknesses = toTopAttributeItem(getTopWeaknesses(attributeValues, midLevelMaturity));

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

    private List<AttributeReportItem> buildAttributes(List<QualityAttributeValue> attributeValues) {
        return attributeValues.stream()
            .map(x -> new AttributeReportItem(x.getQualityAttribute().getId(), x.getMaturityLevel().getId()))
            .toList();
    }

    private List<SubjectReport.TopAttributeItem> toTopAttributeItem(List<Long> topAttributes) {
        return topAttributes
            .stream()
            .map(SubjectReport.TopAttributeItem::new)
            .toList();
    }

}
