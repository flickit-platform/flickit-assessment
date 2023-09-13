package org.flickit.flickitassessmentcore.application.service.subject;

import org.flickit.flickitassessmentcore.adapter.out.rest.maturitylevel.MaturityLevelRestAdapter;
import org.flickit.flickitassessmentcore.application.domain.*;
import org.flickit.flickitassessmentcore.application.domain.mother.MaturityLevelMother;
import org.flickit.flickitassessmentcore.application.domain.report.SubjectReport;
import org.flickit.flickitassessmentcore.application.port.in.subject.ReportSubjectUseCase;
import org.flickit.flickitassessmentcore.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.flickitassessmentcore.application.port.out.qualityattributevalue.LoadAttributeValueListPort;
import org.flickit.flickitassessmentcore.application.port.out.subject.LoadSubjectByAssessmentKitIdPort;
import org.flickit.flickitassessmentcore.application.port.out.subjectvalue.LoadSubjectValuePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toMap;
import static org.flickit.flickitassessmentcore.application.domain.mother.AssessmentResultMother.validResultWithSubjectValuesAndMaturityLevel;
import static org.flickit.flickitassessmentcore.application.domain.mother.MaturityLevelMother.*;
import static org.flickit.flickitassessmentcore.application.domain.mother.QualityAttributeMother.simpleAttribute;
import static org.flickit.flickitassessmentcore.application.domain.mother.QualityAttributeValueMother.withAttributeAndMaturityLevel;
import static org.flickit.flickitassessmentcore.application.domain.mother.SubjectValueMother.withQAValuesAndMaturityLevelAndSubjectWithQAs;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportSubjectServiceTest {

    @InjectMocks
    private ReportSubjectService service;

    @Mock
    private LoadAssessmentResultPort loadAssessmentResultPort;

    @Mock
    private LoadSubjectValuePort loadSubjectValuePort;

    @Mock
    private LoadAttributeValueListPort loadAttributeValueListPort;

    @Mock
    private MaturityLevelRestAdapter maturityLevelRestAdapter;

    @Mock
    private LoadSubjectByAssessmentKitIdPort loadSubjectByAssessmentKitIdPort;

    @Test
    void reportSubject_ValidResult() {
        QualityAttribute attribute1 = simpleAttribute();
        QualityAttribute attribute2 = simpleAttribute();
        QualityAttribute attribute3 = simpleAttribute();
        QualityAttribute attribute4 = simpleAttribute();
        QualityAttribute attribute5 = simpleAttribute();
        List<QualityAttribute> attributes = List.of(
            attribute1,
            attribute2,
            attribute3,
            attribute4,
            attribute5
        );
        List<QualityAttributeValue> qaValues = List.of(
            withAttributeAndMaturityLevel(attribute1, levelOne()),
            withAttributeAndMaturityLevel(attribute2, levelTwo()),
            withAttributeAndMaturityLevel(attribute3, levelThree()),
            withAttributeAndMaturityLevel(attribute4, levelFour()),
            withAttributeAndMaturityLevel(attribute5, levelFive())
        );
        SubjectValue subjectValue = withQAValuesAndMaturityLevelAndSubjectWithQAs
            (qaValues, MaturityLevelMother.levelThree(), attributes);
        AssessmentResult assessmentResult = validResultWithSubjectValuesAndMaturityLevel(
            List.of(subjectValue), levelTwo());

        Map<Long, MaturityLevel> maturityLevels = assessmentResult.getAssessment().getAssessmentKit().getMaturityLevels()
            .stream()
            .collect(toMap(MaturityLevel::getId, x -> x));
        ReportSubjectUseCase.Param param = new ReportSubjectUseCase.Param(
            assessmentResult.getAssessment().getId(),
            subjectValue.getSubject().getId());

        when(loadAssessmentResultPort.loadByAssessmentId(assessmentResult.getAssessment().getId()))
            .thenReturn(Optional.of(assessmentResult));
        when(loadSubjectValuePort.load(subjectValue.getSubject().getId(), assessmentResult.getId()))
            .thenReturn(Optional.of(subjectValue));
        when(loadAttributeValueListPort.loadAttributeValues(assessmentResult.getId(), maturityLevels)).thenReturn(qaValues);
        when(maturityLevelRestAdapter.loadByKitId(assessmentResult.getAssessment().getAssessmentKit().getId()))
            .thenReturn(maturityLevels.values().stream().toList());
        when(loadSubjectByAssessmentKitIdPort.loadByAssessmentKitId(assessmentResult.getAssessment().getAssessmentKit().getId()))
            .thenReturn(List.of(subjectValue.getSubject()));

        SubjectReport subjectReport = service.reportSubject(param);

        assertNotNull(subjectReport);
        assertNotNull(subjectReport.subject());
        assertEquals(subjectValue.getSubject().getId(), subjectReport.subject().id());
        assertEquals(subjectValue.getMaturityLevel().getId(), subjectReport.subject().maturityLevelId());
        assertEquals(assessmentResult.isValid(), subjectReport.subject().isCalculateValid());

        assertEquals(qaValues.size(), subjectReport.attributes().size());

        assertNotNull(subjectReport.topStrengths());
        assertEquals(3, subjectReport.topStrengths().size());

        assertNotNull(subjectReport.topWeaknesses());
        assertEquals(2, subjectReport.topWeaknesses().size());
    }
}
