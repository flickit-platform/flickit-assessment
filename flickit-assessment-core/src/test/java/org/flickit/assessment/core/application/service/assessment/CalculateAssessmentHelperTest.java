package org.flickit.assessment.core.application.service.assessment;

import org.flickit.assessment.core.application.domain.*;
import org.flickit.assessment.core.application.port.out.assessment.UpdateAssessmentPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadCalculateInfoPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.UpdateCalculatedResultPort;
import org.flickit.assessment.core.application.port.out.attributevalue.CreateAttributeValuePort;
import org.flickit.assessment.core.application.port.out.subject.LoadSubjectsPort;
import org.flickit.assessment.core.application.port.out.subjectvalue.CreateSubjectValuePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.flickit.assessment.core.test.fixture.application.AssessmentResultMother.invalidResultWithSubjectValues;
import static org.flickit.assessment.core.test.fixture.application.AttributeValueMother.hasFullScoreOnLevel23WithWeight;
import static org.flickit.assessment.core.test.fixture.application.AttributeValueMother.hasFullScoreOnLevel24WithWeight;
import static org.flickit.assessment.core.test.fixture.application.MaturityLevelMother.levelThree;
import static org.flickit.assessment.core.test.fixture.application.MaturityLevelMother.levelTwo;
import static org.flickit.assessment.core.test.fixture.application.SubjectValueMother.withAttributeValues;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CalculateAssessmentHelperTest {

    @InjectMocks
    private CalculateAssessmentHelper helper;

    @Mock
    private LoadCalculateInfoPort loadCalculateInfoPort;

    @Mock
    private UpdateCalculatedResultPort updateCalculatedResultPort;

    @Mock
    private UpdateAssessmentPort updateAssessmentPort;

    @Mock
    private LoadSubjectsPort loadSubjectsPort;

    @Mock
    private CreateSubjectValuePort createSubjectValuePort;

    @Mock
    private CreateAttributeValuePort createAttributeValuePort;

    @Test
    void testCalculateMaturityLevel_ValidInput_ValidResults() {
        LocalDateTime kitLastMajorModificationTime = LocalDateTime.now();

        // weighted mean scores of attributeValues on levels: 1:0, 2:100, 3:600/10=75, 4:400/100=40, 5:0 => level three passes
        List<AttributeValue> s1AttributeValues = List.of(
            hasFullScoreOnLevel24WithWeight(2, 1533),
            hasFullScoreOnLevel24WithWeight(2, 1534),
            hasFullScoreOnLevel23WithWeight(3, 1535),
            hasFullScoreOnLevel23WithWeight(3, 1536)
        );

        // weighted mean scores of attributeValues on levels: 1:0, 2:100, 3:100/5=25, 4:400/5=80, 5:0 => level two passes
        List<AttributeValue> s2AttributeValues = List.of(
            hasFullScoreOnLevel24WithWeight(4, 1537),
            hasFullScoreOnLevel23WithWeight(1, 1538)
        );

        List<SubjectValue> subjectValues = List.of(
            withAttributeValues(s1AttributeValues, 1),
            withAttributeValues(s2AttributeValues, 5)
        );

        // weighted mean scores of subjectValues on levels: 1:0, 2: 100, 3: (75+(25*5))/6=33.3,  4: (40+(5*80))/6=73, 5:0 => level two passes
        AssessmentResult assessmentResult = invalidResultWithSubjectValues(subjectValues);
        assessmentResult.setLastCalculationTime(LocalDateTime.now());

        when(loadCalculateInfoPort.load(assessmentResult.getAssessment().getId())).thenReturn(assessmentResult);

        var result = helper.calculateMaturityLevel(assessmentResult, kitLastMajorModificationTime);
        verify(updateCalculatedResultPort, times(1)).updateCalculatedResult(any(AssessmentResult.class));
        verify(updateAssessmentPort, times(1)).updateLastModificationTime(any(), any());

        assertNotNull(result);
        assertEquals(levelTwo().getValue(), result.getValue());
    }

    @Test
    void testCalculateMaturityLevel_KitChanged_CreatesNewAttributeAnSubjectValuesAndCalculates() {
        // weighted mean scores of attributeValues on levels: 1:0, 2:100, 3:600/10=75, 4:400/100=40, 5:0 => level three passes
        List<AttributeValue> s1AttributeValues = List.of(
            hasFullScoreOnLevel24WithWeight(2, 1533),
            hasFullScoreOnLevel24WithWeight(2, 1534),
            hasFullScoreOnLevel23WithWeight(3, 1535),
            hasFullScoreOnLevel23WithWeight(3, 1536)
        );

        // weighted mean scores of attributeValues on levels: 1:0, 2:100, 3:100/5=25, 4:400/5=80, 5:0 => level two passes
        List<AttributeValue> s2AttributeValues = List.of(
            hasFullScoreOnLevel24WithWeight(4, 1537),
            hasFullScoreOnLevel23WithWeight(1, 1538)
        );

        List<SubjectValue> subjectValues = List.of(
            withAttributeValues(s1AttributeValues, 5),
            withAttributeValues(s2AttributeValues, 1)
        );

        List<Subject> subjects = new ArrayList<>(subjectValues.stream().map(SubjectValue::getSubject).toList());
        var newAttributeValue = hasFullScoreOnLevel23WithWeight(4, 1533);
        // weighted mean scores of new subjectValue on levels: 1:0, 2:100, 3:100, 4:0, 5:0 => level two passes
        var newSubjectValue = withAttributeValues(List.of(newAttributeValue), 2);
        subjects.add(newSubjectValue.getSubject());

        // weighted mean scores of subjectValues on levels: 1:0, 2:100, 3: ((75*5)+25+(100*2))/8=75,  4: ((40*5)+5+0)/8=25.6, 5:0 => level three passes
        AssessmentResult assessmentResult = invalidResultWithSubjectValues(subjectValues);
        assessmentResult.setLastCalculationTime(LocalDateTime.now());
        var kitLastMajorModificationTime = LocalDateTime.now();

        when(loadCalculateInfoPort.load(assessmentResult.getAssessment().getId())).thenReturn(assessmentResult);
        when(loadSubjectsPort.loadByKitVersionIdWithAttributes(any())).thenReturn(subjects);
        when(createSubjectValuePort.persistAll(anyList(), any())).thenReturn(List.of(newSubjectValue));
        when(createAttributeValuePort.persistAll(anySet(), any())).thenReturn(List.of(newAttributeValue));

        var result = helper.calculateMaturityLevel(assessmentResult, kitLastMajorModificationTime);
        verify(updateCalculatedResultPort, times(1)).updateCalculatedResult(any(AssessmentResult.class));
        verify(updateAssessmentPort, times(1)).updateLastModificationTime(any(), any());

        assertNotNull(result);
        assertEquals(levelThree().getValue(), result.getValue());
    }
}
