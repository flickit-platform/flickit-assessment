package org.flickit.assessment.core.application.service.assessmentreport;

import org.flickit.assessment.core.application.domain.*;
import org.flickit.assessment.core.application.port.out.assessmentkit.LoadKitLastMajorModificationTimePort;
import org.flickit.assessment.core.application.port.out.attributevalue.CreateAttributeValuePort;
import org.flickit.assessment.core.application.port.out.subject.LoadSubjectsPort;
import org.flickit.assessment.core.application.port.out.subjectvalue.CreateSubjectValuePort;
import org.flickit.assessment.core.test.fixture.application.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InitializeAssessmentResultHelperTest {

    @InjectMocks
    private InitializeAssessmentResultHelper helper;

    @Mock
    private LoadKitLastMajorModificationTimePort loadKitLastMajorModificationTimePort;

    @Mock
    private LoadSubjectsPort loadSubjectsPort;

    @Mock
    private CreateSubjectValuePort createSubjectValuePort;

    @Mock
    private CreateAttributeValuePort createAttributeValuePort;

    private final AssessmentResult assessmentResult = AssessmentResultMother.invalidResultWithSubjectValues(
        List.of(SubjectValueMother.createSubjectValue(), SubjectValueMother.createSubjectValue()));
    private final LocalDateTime now = LocalDateTime.now();

    @Test
    void testReinitializeAssessmentResult_whenReinitializeNotRequired_thenShouldNotReinitialize() {
        when(loadKitLastMajorModificationTimePort.loadLastMajorModificationTime(assessmentResult.getAssessment().getAssessmentKit().getId()))
            .thenReturn(now.minusDays(1));

        helper.reinitializeAssessmentResultIfRequired(assessmentResult);

        verifyNoInteractions(loadSubjectsPort,
            createSubjectValuePort,
            createAttributeValuePort);
    }

    @Test
    void testReinitializeAssessmentResult_whenReinitializeRequired_thenShouldReinitialize() {
        var oldSubjectValues = assessmentResult.getSubjectValues();
        var attributes = List.of(AttributeMother.simpleAttribute(), AttributeMother.simpleAttribute());
        var newSubjectWithNewAttribute = SubjectMother.subjectWithWeightAndAttributes(1, attributes);
        var newSubjects = List.of(newSubjectWithNewAttribute);
        assessmentResult.setLastConfidenceCalculationTime(now.minusDays(1));
        var newSubjectValueId = UUID.randomUUID();
        var newSubjectValue = new SubjectValue(newSubjectValueId, newSubjectWithNewAttribute, new ArrayList<>());
        var newAttributeValue1 = AttributeValueMother.toBeCalcWithAttributeAndAnswers(attributes.getFirst(), null);
        var newAttributeValue2 = AttributeValueMother.toBeCalcWithAttributeAndAnswers(attributes.getLast(), null);

        when(loadSubjectsPort.loadByKitVersionIdWithAttributes(assessmentResult.getKitVersionId())).thenReturn(newSubjects);
        when(loadKitLastMajorModificationTimePort.loadLastMajorModificationTime(assessmentResult.getAssessment().getAssessmentKit().getId()))
            .thenReturn(now);
        when(createSubjectValuePort.persistAll(any(), eq(assessmentResult.getId()))).thenReturn(List.of(newSubjectValue));
        when(createAttributeValuePort.persistAll(any(), eq(assessmentResult.getId()))).thenReturn(List.of(newAttributeValue1, newAttributeValue2));

        helper.reinitializeAssessmentResultIfRequired(assessmentResult);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<Long>> subjectCaptor = ArgumentCaptor.forClass(List.class);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Set<Long>> attributeCaptor = ArgumentCaptor.forClass(Set.class);

        verify(createSubjectValuePort).persistAll(subjectCaptor.capture(), eq(assessmentResult.getId()));
        verify(createAttributeValuePort).persistAll(attributeCaptor.capture(), eq(assessmentResult.getId()));

        assertEquals(3, assessmentResult.getSubjectValues().size());
        assertEquals(1, subjectCaptor.getValue().size());
        assertTrue(subjectCaptor.getValue().contains(newSubjectWithNewAttribute.getId()));
        assertEquals(2, attributeCaptor.getValue().size());
        assertTrue(attributeCaptor.getValue().contains(newAttributeValue1.getAttribute().getId()));
        assertTrue(attributeCaptor.getValue().contains(newAttributeValue2.getAttribute().getId()));
        assertTrue(assessmentResult.getSubjectValues().containsAll(oldSubjectValues));
        assertTrue(assessmentResult.getSubjectValues().stream()
            .anyMatch(sv ->
                sv.getId().equals(newSubjectValueId) &&
                    sv.getAttributeValues().contains(newAttributeValue1) &&
                    sv.getAttributeValues().contains(newAttributeValue2)));
    }
}

