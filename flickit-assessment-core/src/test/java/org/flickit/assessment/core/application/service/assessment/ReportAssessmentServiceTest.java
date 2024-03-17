package org.flickit.assessment.core.application.service.assessment;

import org.flickit.assessment.core.application.domain.*;
import org.flickit.assessment.core.application.domain.report.AssessmentReport;
import org.flickit.assessment.core.application.internal.ValidateAssessmentResult;
import org.flickit.assessment.core.application.port.in.assessment.ReportAssessmentUseCase;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentReportInfoPort;
import org.flickit.assessment.core.application.port.out.qualityattributevalue.LoadAttributeValueListPort;
import org.flickit.assessment.core.test.fixture.application.MaturityLevelMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;
import static org.flickit.assessment.core.test.fixture.application.AssessmentResultMother.validResultWithSubjectValuesAndMaturityLevel;
import static org.flickit.assessment.core.test.fixture.application.MaturityLevelMother.*;
import static org.flickit.assessment.core.test.fixture.application.QualityAttributeMother.simpleAttribute;
import static org.flickit.assessment.core.test.fixture.application.QualityAttributeValueMother.withAttributeAndMaturityLevel;
import static org.flickit.assessment.core.test.fixture.application.SubjectValueMother.withQAValuesAndMaturityLevel;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportAssessmentServiceTest {

    @InjectMocks
    private ReportAssessmentService service;

    @Mock
    private ValidateAssessmentResult validateAssessmentResult;

    @Mock
    private LoadAssessmentReportInfoPort loadReportInfoPort;

    @Mock
    private LoadAttributeValueListPort loadAttributeValueListPort;

    @Test
    void testReportAssessment_ValidResult() {
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

        AssessmentKit kit = assessmentResult.getAssessment().getAssessmentKit();
        Map<Long, MaturityLevel> maturityLevels = kit.getMaturityLevels()
            .stream()
            .collect(toMap(MaturityLevel::getId, x -> x));
        ReportAssessmentUseCase.Param param = new ReportAssessmentUseCase.Param(assessmentResult.getAssessment().getId());

        when(loadReportInfoPort.load(assessmentResult.getAssessment().getId())).thenReturn(assessmentResult);
        when(loadAttributeValueListPort.loadAll(assessmentResult.getId(), maturityLevels)).thenReturn(qaValues);

        doNothing().when(validateAssessmentResult).validate(assessmentResult.getAssessment().getId());

        AssessmentReport assessmentReport = service.reportAssessment(param);

        assertNotNull(assessmentReport);
        assertNotNull(assessmentReport.assessment());
        assertEquals(assessmentResult.getAssessment().getId(), assessmentReport.assessment().id());
        assertEquals(assessmentResult.getAssessment().getTitle(), assessmentReport.assessment().title());
        assertEquals(assessmentResult.getMaturityLevel().getId(), assessmentReport.assessment().maturityLevelId());
        assertEquals(assessmentResult.getAssessment().getColorId(), assessmentReport.assessment().color().getId());
        assertEquals(assessmentResult.getIsCalculateValid(), assessmentReport.assessment().isCalculateValid());
        assertEquals(assessmentResult.getAssessment().getLastModificationTime(), assessmentReport.assessment().lastModificationTime());

        assertNotNull(assessmentReport.topStrengths());
        assertEquals(3, assessmentReport.topStrengths().size());

        assertNotNull(assessmentReport.topWeaknesses());
        assertEquals(2, assessmentReport.topWeaknesses().size());
    }
}
