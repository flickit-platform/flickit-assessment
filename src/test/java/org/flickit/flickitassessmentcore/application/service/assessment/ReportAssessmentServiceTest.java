package org.flickit.flickitassessmentcore.application.service.assessment;

import org.flickit.flickitassessmentcore.application.domain.AssessmentResult;
import org.flickit.flickitassessmentcore.application.domain.MaturityLevel;
import org.flickit.flickitassessmentcore.application.domain.QualityAttributeValue;
import org.flickit.flickitassessmentcore.application.domain.SubjectValue;
import org.flickit.flickitassessmentcore.application.domain.mother.MaturityLevelMother;
import org.flickit.flickitassessmentcore.application.domain.report.AssessmentReport;
import org.flickit.flickitassessmentcore.application.port.in.assessment.ReportAssessmentUseCase;
import org.flickit.flickitassessmentcore.application.port.out.assessmentresult.LoadAssessmentReportInfoPort;
import org.flickit.flickitassessmentcore.application.port.out.qualityattributevalue.LoadAttributeValueListPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;
import static org.flickit.flickitassessmentcore.application.domain.mother.AssessmentResultMother.validResultWithSubjectValuesAndMaturityLevel;
import static org.flickit.flickitassessmentcore.application.domain.mother.MaturityLevelMother.*;
import static org.flickit.flickitassessmentcore.application.domain.mother.QualityAttributeMother.simpleAttribute;
import static org.flickit.flickitassessmentcore.application.domain.mother.QualityAttributeValueMother.withAttributeAndMaturityLevel;
import static org.flickit.flickitassessmentcore.application.domain.mother.SubjectValueMother.withQAValuesAndMaturityLevel;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportAssessmentServiceTest {

    @InjectMocks
    private ReportAssessmentService service;

    @Mock
    private LoadAssessmentReportInfoPort loadReportInfoPort;

    @Mock
    private LoadAttributeValueListPort loadAttributeValueListPort;

    @Test
    void reportAssessment_ValidResult() {
        List<QualityAttributeValue> qaValues = List.of(
            withAttributeAndMaturityLevel(simpleAttribute(), levelOne()),
            withAttributeAndMaturityLevel(simpleAttribute(), levelTwo()),
            withAttributeAndMaturityLevel(simpleAttribute(), levelThree()),
            withAttributeAndMaturityLevel(simpleAttribute(), levelFour()),
            withAttributeAndMaturityLevel(simpleAttribute(), levelFive())
        );
        SubjectValue subjectValue = withQAValuesAndMaturityLevel(qaValues, MaturityLevelMother.levelThree());
        AssessmentResult assessmentResult = validResultWithSubjectValuesAndMaturityLevel(
            List.of(subjectValue), levelTwo());

        Map<Long, MaturityLevel> maturityLevels = assessmentResult.getAssessment().getAssessmentKit().getMaturityLevels()
            .stream()
            .collect(toMap(MaturityLevel::getId, x -> x));
        ReportAssessmentUseCase.Param param = new ReportAssessmentUseCase.Param(assessmentResult.getAssessment().getId());

        when(loadReportInfoPort.load(assessmentResult.getAssessment().getId())).thenReturn(assessmentResult);
        when(loadAttributeValueListPort.loadAttributeValues(assessmentResult.getId(), maturityLevels)).thenReturn(qaValues);

        AssessmentReport assessmentReport = service.reportAssessment(param);

        assertNotNull(assessmentReport);
        assertNotNull(assessmentReport.assessment());
        assertEquals(assessmentResult.getAssessment().getId(), assessmentReport.assessment().id());
        assertEquals(assessmentResult.getAssessment().getTitle(), assessmentReport.assessment().title());
        assertEquals(assessmentResult.getMaturityLevel().getId(), assessmentReport.assessment().maturityLevelId());
        assertEquals(assessmentResult.getAssessment().getColorId(), assessmentReport.assessment().colorId());
        assertEquals(assessmentResult.isValid(), assessmentReport.assessment().isCalculateValid());
        assertEquals(assessmentResult.getAssessment().getLastModificationTime(), assessmentReport.assessment().lastModificationTime());

        assertNotNull(assessmentReport.topStrengths());
        assertEquals(3, assessmentReport.topStrengths().size());

        assertNotNull(assessmentReport.topWeaknesses());
        assertEquals(2, assessmentReport.topWeaknesses().size());
    }
}
