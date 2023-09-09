package org.flickit.flickitassessmentcore.application.service.subject;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.domain.MaturityLevel;
import org.flickit.flickitassessmentcore.application.domain.QualityAttributeValue;
import org.flickit.flickitassessmentcore.application.domain.SubjectValue;
import org.flickit.flickitassessmentcore.application.domain.report.SubjectReport;
import org.flickit.flickitassessmentcore.application.domain.report.SubjectReport.AttributeReportItem;
import org.flickit.flickitassessmentcore.application.domain.report.SubjectReport.QualityAttributeReportItem;
import org.flickit.flickitassessmentcore.application.port.in.subject.ReportSubjectUseCase;
import org.flickit.flickitassessmentcore.application.port.out.assessmentresult.LoadAssessmentResultBySubjectValueId;
import org.flickit.flickitassessmentcore.application.port.out.subject.LoadSubjectTitle;
import org.flickit.flickitassessmentcore.application.port.out.subjectvalue.LoadSubjectValueBySubjectIdPort;
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
public class ReportSubjectService implements ReportSubjectUseCase {

    private static final int TOP_COUNT = 3;
    private final LoadSubjectTitle loadSubjectTitle;
    private final LoadAssessmentResultBySubjectValueId loadAssessmentResultBySubjectValueId;
    private final LoadSubjectValueBySubjectIdPort loadSubjectValueBySubjectIdPort;

    @Override
    public SubjectReport reportSubject(Param param) {
        var subjectValue = loadSubjectValueBySubjectIdPort.load(param.getSubjectId());
        String subjectTitle = loadSubjectTitle.load(param.getSubjectId());
        var result = loadAssessmentResultBySubjectValueId.load(subjectValue.getId());
        var maturityLevels = result.getAssessment().getAssessmentKit().getMaturityLevels();
        var attributeValues = subjectValue.getQualityAttributeValues();

        var subjectReportItem = buildSubject(subjectValue, subjectTitle, result.isValid());
        var attributeReportItems = buildAttributes(subjectValue);
        var midLevelMaturity = middleLevel(maturityLevels);
        var topStrengths = getTopStrengths(attributeValues, midLevelMaturity);
        var topWeaknesses = getTopWeaknesses(attributeValues, midLevelMaturity);

        return new SubjectReport(
            subjectReportItem,
            topStrengths,
            topWeaknesses,
            attributeReportItems);
    }

    private SubjectReport.SubjectReportItem buildSubject(SubjectValue subjectValue, String title, boolean isCalculateValid) {
        return new SubjectReport.SubjectReportItem(
            subjectValue.getSubject().getId(),
            title,
            subjectValue.getMaturityLevel().getId(),
            isCalculateValid
        );
    }

    private List<QualityAttributeReportItem> buildAttributes(SubjectValue subjectValue) {
        return subjectValue.getQualityAttributeValues()
            .stream()
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
