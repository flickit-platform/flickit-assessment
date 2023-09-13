package org.flickit.flickitassessmentcore.application.service.subject;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.flickitassessmentcore.adapter.out.rest.maturitylevel.MaturityLevelRestAdapter;
import org.flickit.flickitassessmentcore.application.domain.*;
import org.flickit.flickitassessmentcore.application.domain.report.SubjectReport;
import org.flickit.flickitassessmentcore.application.domain.report.SubjectReport.AttributeReportItem;
import org.flickit.flickitassessmentcore.application.port.in.subject.ReportSubjectUseCase;
import org.flickit.flickitassessmentcore.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.flickitassessmentcore.application.port.out.qualityattributevalue.LoadAttributeValueListPort;
import org.flickit.flickitassessmentcore.application.port.out.subject.LoadSubjectByAssessmentKitIdPort;
import org.flickit.flickitassessmentcore.application.port.out.subjectvalue.LoadSubjectValuePort;
import org.flickit.flickitassessmentcore.application.service.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;
import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.REPORT_SUBJECT_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.REPORT_SUBJECT_ASSESSMENT_SUBJECT_VALUE_NOT_FOUND;
import static org.flickit.flickitassessmentcore.common.report.EntityReportCommonCalculations.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ReportSubjectService implements ReportSubjectUseCase {

    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final LoadSubjectValuePort loadSubjectValuePort;
    private final LoadAttributeValueListPort loadAttributeValueListPort;
    private final MaturityLevelRestAdapter maturityLevelRestAdapter;
    private final LoadSubjectByAssessmentKitIdPort loadSubjectByAssessmentKitIdPort;

    @Override
    public SubjectReport reportSubject(Param param) {
        var result = loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(REPORT_SUBJECT_ASSESSMENT_RESULT_NOT_FOUND));
        var subjectValue = loadSubjectValuePort.load(
            param.getSubjectId(),
            result.getId())
            .orElseThrow(() -> new ResourceNotFoundException(REPORT_SUBJECT_ASSESSMENT_SUBJECT_VALUE_NOT_FOUND));

        var maturityLevels = maturityLevelRestAdapter.loadByKitId(result.getAssessment().getAssessmentKit().getId());
        var attributeValues = buildAttributeValues(maturityLevels, result, param.getSubjectId());

        var subjectReportItem = buildSubject(subjectValue, result.isValid());
        var attributeReportItems = buildAttributes(attributeValues);
        var midLevelMaturity = middleLevel(maturityLevels);
        var topStrengths = mapToSubjectReportAttrItem(getTopStrengths(attributeValues, midLevelMaturity));
        var topWeaknesses = mapToSubjectReportAttrItem(getTopWeaknesses(attributeValues, midLevelMaturity));

        return new SubjectReport(
            subjectReportItem,
            topStrengths,
            topWeaknesses,
            attributeReportItems);
    }

    private List<QualityAttributeValue> buildAttributeValues(List<MaturityLevel> maturityLevels, AssessmentResult result, Long subjectId) {
        Map<Long, MaturityLevel> maturityLevelMap = maturityLevels.stream()
            .collect(toMap(MaturityLevel::getId, x -> x));
        var subject = loadSubjectByAssessmentKitIdPort.loadByAssessmentKitId(result.getAssessment().getAssessmentKit().getId())
            .stream()
            .filter(x -> x.getId() == subjectId)
            .toList()
            .get(0);
        Map<Long, QualityAttribute> qualityAttributeMap = subject.getQualityAttributes()
            .stream()
            .collect(toMap(QualityAttribute::getId, x -> x));
        return loadAttributeValueListPort.loadAttributeValues(result.getId(), maturityLevelMap)
            .stream()
            .filter(x -> qualityAttributeMap.containsKey(x.getQualityAttribute().getId()))
            .toList();
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

    private List<SubjectReport.TopAttributeItem> mapToSubjectReportAttrItem(List<Long> attributeValues) {
        return attributeValues
            .stream()
            .map(SubjectReport.TopAttributeItem::new)
            .toList();
    }

}
