package org.flickit.flickitassessmentcore.application.service.subject;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.flickitassessmentcore.adapter.out.rest.maturitylevel.MaturityLevelRestAdapter;
import org.flickit.flickitassessmentcore.application.domain.*;
import org.flickit.flickitassessmentcore.application.domain.report.SubjectReport;
import org.flickit.flickitassessmentcore.application.domain.report.SubjectReport.AttributeReportItem;
import org.flickit.flickitassessmentcore.application.domain.report.SubjectReport.QualityAttributeReportItem;
import org.flickit.flickitassessmentcore.application.port.in.subject.ReportSubjectUseCase;
import org.flickit.flickitassessmentcore.application.port.out.assessment.LoadAssessmentPort;
import org.flickit.flickitassessmentcore.application.port.out.assessmentresult.LoadAssessmentResultBySubjectValueIdPort;
import org.flickit.flickitassessmentcore.application.port.out.qualityattributevalue.LoadAttributeValueListPort;
import org.flickit.flickitassessmentcore.application.port.out.subject.LoadSubjectByAssessmentKitIdPort;
import org.flickit.flickitassessmentcore.application.port.out.subjectvalue.LoadSubjectValueBySubjectIdPort;
import org.flickit.flickitassessmentcore.application.service.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static java.util.Collections.reverseOrder;
import static java.util.Comparator.comparing;
import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.toMap;
import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.REPORT_SUBJECT_ASSESSMENT_RESULT_NOT_FOUND;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ReportSubjectService implements ReportSubjectUseCase {

    private static final int TOP_COUNT = 3;

    private final LoadAssessmentResultBySubjectValueIdPort loadAssessmentResultBySubjectValueIdPort;
    private final LoadSubjectValueBySubjectIdPort loadSubjectValueBySubjectIdPort;
    private final LoadAttributeValueListPort loadAttributeValueListPort;
    private final MaturityLevelRestAdapter maturityLevelRestAdapter;
    private final LoadAssessmentPort loadAssessmentPort;
    private final LoadSubjectByAssessmentKitIdPort loadSubjectByAssessmentKitIdPort;


    @Override
    public SubjectReport reportSubject(Param param) {
        var subjectValue = loadSubjectValueBySubjectIdPort.load(param.getSubjectId());
        var result = loadAssessmentResultBySubjectValueIdPort.load(subjectValue.getId())
            .orElseThrow(() -> new ResourceNotFoundException(REPORT_SUBJECT_ASSESSMENT_RESULT_NOT_FOUND));
        var assessment = loadAssessmentPort.loadAssessment(result.getAssessment().getId());

        var maturityLevels = maturityLevelRestAdapter.loadByKitId(assessment.getAssessmentKit().getId());
        var attributeValues = buildQualityAttributes(maturityLevels, result, assessment.getAssessmentKit(), param.getSubjectId());

        var subjectReportItem = buildSubject(subjectValue, result.isValid());
        var attributeReportItems = buildAttributes(attributeValues);
        var midLevelMaturity = middleLevel(maturityLevels);
        var topStrengths = getTopStrengths(attributeValues, midLevelMaturity);
        var topWeaknesses = getTopWeaknesses(attributeValues, midLevelMaturity);

        return new SubjectReport(
            subjectReportItem,
            topStrengths,
            topWeaknesses,
            attributeReportItems);
    }

    private List<QualityAttributeValue> buildQualityAttributes(List<MaturityLevel> maturityLevels, AssessmentResult result, AssessmentKit kit, Long subjectId) {
        Map<Long, MaturityLevel> maturityLevelMap = maturityLevels.stream()
            .collect(toMap(MaturityLevel::getId, x -> x));
        var subject = loadSubjectByAssessmentKitIdPort.loadByAssessmentKitId(kit.getId())
            .stream()
            .filter(x -> x.getId() == subjectId)
            .toList()
            .get(0);
        Map<Long, QualityAttribute> qualityAttributeMap = subject.getQualityAttributes()
            .stream()
            .collect(toMap(QualityAttribute::getId, x -> x));
        var attributeValues = loadAttributeValueListPort.loadAttributeValues(result.getId(), maturityLevelMap)
            .stream()
            .filter(x -> qualityAttributeMap.containsKey(x.getQualityAttribute().getId()))
            .toList();
        return attributeValues;
    }

    private SubjectReport.SubjectReportItem buildSubject(SubjectValue subjectValue, boolean isCalculateValid) {
        return new SubjectReport.SubjectReportItem(
            subjectValue.getSubject().getId(),
            subjectValue.getMaturityLevel().getId(),
            isCalculateValid
        );
    }

    private List<QualityAttributeReportItem> buildAttributes(List<QualityAttributeValue> attributeValues) {
        return attributeValues.stream()
            .map(x -> new QualityAttributeReportItem(x.getQualityAttribute().getId(), x.getMaturityLevel().getId()))
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
