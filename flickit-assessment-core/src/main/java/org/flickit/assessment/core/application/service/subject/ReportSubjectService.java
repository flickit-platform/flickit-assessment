package org.flickit.assessment.core.application.service.subject;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.domain.MaturityScore;
import org.flickit.assessment.core.application.domain.QualityAttributeValue;
import org.flickit.assessment.core.application.domain.SubjectValue;
import org.flickit.assessment.core.application.domain.report.SubjectReport;
import org.flickit.assessment.core.application.domain.report.SubjectReport.AttributeReportItem;
import org.flickit.assessment.core.application.domain.report.TopAttributeResolver;
import org.flickit.assessment.core.application.port.in.subject.ReportSubjectUseCase;
import org.flickit.assessment.core.application.port.out.subject.LoadSubjectReportInfoPort;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadKitLastEffectiveModificationTimePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;

import static java.util.stream.Collectors.toCollection;
import static org.flickit.assessment.core.application.domain.MaturityLevel.middleLevel;
import static org.flickit.assessment.core.common.ErrorMessageKey.REPORT_SUBJECT_ASSESSMENT_SUBJECT_VALUE_NOT_FOUND;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReportSubjectService implements ReportSubjectUseCase {

    private final LoadSubjectReportInfoPort loadSubjectReportInfoPort;
    private final LoadKitLastEffectiveModificationTimePort loadKitLastEffectiveModificationTimePort;

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

        LocalDateTime kitLastEffectiveModificationTime = loadKitLastEffectiveModificationTimePort.load(assessmentResult.getAssessment().getAssessmentKit().getId());
        var subjectReportItem = buildSubject(subjectValue, assessmentResult, kitLastEffectiveModificationTime);
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

    private SubjectReport.SubjectReportItem buildSubject(SubjectValue subjectValue, AssessmentResult assessmentResult, LocalDateTime kitLastEffectiveModificationTime) {
        return new SubjectReport.SubjectReportItem(
            subjectValue.getSubject().getId(),
            subjectValue.getMaturityLevel().getId(),
            subjectValue.getConfidenceValue(),
            isValid(assessmentResult.isCalculateValid(), assessmentResult.getLastCalculationTime(), kitLastEffectiveModificationTime),
            isValid(assessmentResult.isConfidenceValid(), assessmentResult.getLastCalculationTime(), kitLastEffectiveModificationTime)
        );
    }

    private boolean isValid(boolean isValid, LocalDateTime lastCalculationTime, LocalDateTime kitLastEffectiveModificationTime) {
        return isValid || lastCalculationTime.isBefore(kitLastEffectiveModificationTime);
    }


    private List<AttributeReportItem> buildAttributes(List<QualityAttributeValue> attributeValues) {
        return attributeValues.stream()
            .map(x -> new AttributeReportItem(
                x.getQualityAttribute().getId(),
                x.getMaturityLevel().getId(),
                x.getMaturityScores().stream()
                    .sorted(Comparator.comparingLong(MaturityScore::getMaturityLevelId))
                    .collect(toCollection(LinkedHashSet::new)),
                x.getConfidenceValue()
            ))
            .toList();
    }
}
