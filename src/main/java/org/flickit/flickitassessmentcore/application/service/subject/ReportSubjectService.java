package org.flickit.flickitassessmentcore.application.service.subject;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.domain.MaturityLevel;
import org.flickit.flickitassessmentcore.application.domain.QualityAttributeValue;
import org.flickit.flickitassessmentcore.application.domain.SubjectValue;
import org.flickit.flickitassessmentcore.application.domain.report.AssessmentReport.AttributeReportItem;
import org.flickit.flickitassessmentcore.application.domain.report.SubjectReport;
import org.flickit.flickitassessmentcore.application.domain.report.SubjectReport.QualityAttributeReportItem;
import org.flickit.flickitassessmentcore.application.port.in.subject.ReportSubjectUseCase;
import org.flickit.flickitassessmentcore.application.port.out.qualityattributevalue.LoadAttributeValueListPort;
import org.flickit.flickitassessmentcore.application.port.out.subjectvalue.LoadSubjectValueBySubjectIdPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.reverseOrder;
import static java.util.Comparator.comparing;
import static java.util.Comparator.comparingInt;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReportSubjectService implements ReportSubjectUseCase {

    private static final int TOP_COUNT = 3;
    private final LoadAttributeValueListPort loadAttributeValueListPort;
    private final LoadSubjectValueBySubjectIdPort loadSubjectValueBySubjectIdPort;

    @Override
    public SubjectReport reportSubject(Param param) {
        var subjectValue = loadSubjectValueBySubjectIdPort.load(param.getSubjectId());

//        var maturityLevels = subjectValue.getAssessment().getAssessmentKit().getMaturityLevels();
//        Map<Long, MaturityLevel> maturityLevelsMap = maturityLevels.stream()
//            .collect(toMap(MaturityLevel::getId, x -> x));

//        var attributeValues = loadAttributeValueListPort.loadAttributeValues(subjectValue.getId(), maturityLevelsMap);

        var subjectReportItem = buildSubject(subjectValue);
        var attributeReportItems = buildAttributes(subjectValue);
//        var midLevelMaturity = middleLevel(maturityLevels);
//        var topStrengths = getTopStrengths(attributeValues, midLevelMaturity);
//        var topWeaknesses = getTopWeaknesses(attributeValues, midLevelMaturity);

        return new SubjectReport(
            subjectReportItem,
            new ArrayList<>(),
            new ArrayList<>(),
            attributeReportItems);
    }

    private SubjectReport.SubjectReportItem buildSubject(SubjectValue subjectValue) {
        return new SubjectReport.SubjectReportItem(
            subjectValue.getSubject().getId(),
            "",
            subjectValue.getMaturityLevel().getId(),
            false
        );
    }

    private List<QualityAttributeReportItem> buildAttributes(SubjectValue subjectValue) {
        return new ArrayList<>();
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
