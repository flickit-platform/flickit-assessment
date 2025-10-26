package org.flickit.assessment.core.application.service.assessment;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.domain.*;
import org.flickit.assessment.core.application.port.in.assessment.CalculateAssessmentUseCase;
import org.flickit.assessment.core.application.port.out.assessment.UpdateAssessmentPort;
import org.flickit.assessment.core.application.port.out.assessmentkit.LoadKitLastMajorModificationTimePort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadCalculateInfoPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.UpdateCalculatedResultPort;
import org.flickit.assessment.core.application.port.out.attributevalue.CreateAttributeValuePort;
import org.flickit.assessment.core.application.port.out.kitcustom.LoadKitCustomLastModificationTimePort;
import org.flickit.assessment.core.application.port.out.subject.LoadSubjectsPort;
import org.flickit.assessment.core.application.port.out.subjectvalue.CreateSubjectValuePort;
import org.flickit.assessment.core.test.fixture.application.AssessmentResultMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.CALCULATE_ASSESSMENT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.test.fixture.application.AssessmentResultMother.invalidResultWithSubjectValues;
import static org.flickit.assessment.core.test.fixture.application.AssessmentResultMother.validResult;
import static org.flickit.assessment.core.test.fixture.application.AttributeValueMother.hasFullScoreOnLevel23WithWeight;
import static org.flickit.assessment.core.test.fixture.application.AttributeValueMother.hasFullScoreOnLevel24WithWeight;
import static org.flickit.assessment.core.test.fixture.application.MaturityLevelMother.levelThree;
import static org.flickit.assessment.core.test.fixture.application.MaturityLevelMother.levelTwo;
import static org.flickit.assessment.core.test.fixture.application.SubjectValueMother.withAttributeValues;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CalculateAssessmentServiceTest {

    @InjectMocks
    private CalculateAssessmentService service;

    @Mock
    private LoadAssessmentResultPort loadAssessmentResultPort;

    @Mock
    private LoadKitLastMajorModificationTimePort loadKitLastMajorModificationTimePort;

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

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private LoadKitCustomLastModificationTimePort loadKitCustomLastModificationTimePort;

    @Test
    void testCalculateMaturityLevel_whenCurrentUserDoesNotHaveRequiredPermission_thenThrowAccessDeniedException() {
        var param = createParam(CalculateAssessmentUseCase.Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CALCULATE_ASSESSMENT)).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.calculateMaturityLevel(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(updateCalculatedResultPort,
            updateAssessmentPort,
            loadCalculateInfoPort,
            createSubjectValuePort,
            createAttributeValuePort,
            loadKitLastMajorModificationTimePort,
            loadKitCustomLastModificationTimePort);
    }

    @Test
    void testCalculateMaturityLevel_whenCalculationIsValid_thenDoNotCalculateAndReturnResultAffectedAsFalse() {
        var param = new CalculateAssessmentUseCase.Param(UUID.randomUUID(), UUID.randomUUID());
        var assessmentResult = validResult();

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CALCULATE_ASSESSMENT)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadKitLastMajorModificationTimePort.loadLastMajorModificationTime(any())).thenReturn(LocalDateTime.MIN);
        when(loadKitCustomLastModificationTimePort.loadLastModificationTime(assessmentResult.getAssessment().getKitCustomId())).thenReturn(LocalDateTime.MIN);

        var result = service.calculateMaturityLevel(param);
        assertFalse(result.resultAffected());
        assertEquals(assessmentResult.getMaturityLevel(), result.maturityLevel());

        verifyNoInteractions(updateCalculatedResultPort,
            updateAssessmentPort,
            loadCalculateInfoPort,
            createSubjectValuePort,
            createAttributeValuePort);
    }

    @Test
    void testCalculateMaturityLevel_whenCalculationIsNotValidAndKitHasNotChanged_thenDoCalculateAndDoNotReinitialize() {
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
        var param = createParam(b -> b.assessmentId(assessmentResult.getAssessment().getId()));

        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadCalculateInfoPort.load(param.getAssessmentId())).thenReturn(assessmentResult);
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CALCULATE_ASSESSMENT)).thenReturn(true);
        when(loadKitLastMajorModificationTimePort.loadLastMajorModificationTime(assessmentResult.getAssessment().getAssessmentKit().getId()))
            .thenReturn(assessmentResult.getLastCalculationTime().minusHours(1));
        when(loadKitCustomLastModificationTimePort.loadLastModificationTime(assessmentResult.getAssessment().getKitCustomId()))
            .thenReturn(assessmentResult.getLastCalculationTime().minusHours(1));

        var result = service.calculateMaturityLevel(param);

        assertNotNull(result);
        assertNotNull(result.maturityLevel());
        assertEquals(levelTwo().getValue(), result.maturityLevel().getValue());
        assertTrue(result.resultAffected());

        verify(updateCalculatedResultPort).updateCalculatedResult(any(AssessmentResult.class));
        verify(updateAssessmentPort).updateLastModificationTime(any(), any());
        verifyNoInteractions(loadSubjectsPort, createSubjectValuePort, createAttributeValuePort);
    }

    @Test
    void testCalculateMaturityLevel_whenCalculationIsValidAndKitCustomChanged_thenDoCalculateAndDoNotReinitialize() {
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
        AssessmentResult assessmentResult = AssessmentResultMother.validResultWithSubjectValuesAndMaturityLevel(subjectValues, levelTwo());
        var param = createParam(b -> b.assessmentId(assessmentResult.getAssessment().getId()));

        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadCalculateInfoPort.load(param.getAssessmentId())).thenReturn(assessmentResult);
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CALCULATE_ASSESSMENT)).thenReturn(true);
        when(loadKitLastMajorModificationTimePort.loadLastMajorModificationTime(assessmentResult.getAssessment().getAssessmentKit().getId()))
            .thenReturn(assessmentResult.getLastCalculationTime().minusHours(1));
        when(loadKitCustomLastModificationTimePort.loadLastModificationTime(assessmentResult.getAssessment().getKitCustomId()))
            .thenReturn(assessmentResult.getLastCalculationTime().plusHours(1));

        var result = service.calculateMaturityLevel(param);

        assertNotNull(result);
        assertNotNull(result.maturityLevel());
        assertEquals(levelTwo().getValue(), result.maturityLevel().getValue());
        assertTrue(result.resultAffected());

        verify(updateCalculatedResultPort).updateCalculatedResult(any(AssessmentResult.class));
        verify(updateAssessmentPort).updateLastModificationTime(any(), any());
        verifyNoInteractions(loadSubjectsPort, createSubjectValuePort, createAttributeValuePort);
    }

    @Test
    void testCalculateMaturityLevel_whenKitHasChanged_thenReinitializeAndCalculate() {
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

        var param = createParam(b -> b.assessmentId(assessmentResult.getAssessment().getId()));

        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadCalculateInfoPort.load(param.getAssessmentId())).thenReturn(assessmentResult);
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CALCULATE_ASSESSMENT)).thenReturn(true);
        when(loadKitLastMajorModificationTimePort.loadLastMajorModificationTime(assessmentResult.getAssessment().getAssessmentKit().getId()))
            .thenReturn(assessmentResult.getLastCalculationTime().plusHours(1));
        when(loadSubjectsPort.loadByKitVersionIdWithAttributes(any())).thenReturn(subjects);
        when(createSubjectValuePort.persistAll(List.of(newSubjectValue.getSubject().getId()), assessmentResult.getId()))
            .thenReturn(List.of(newSubjectValue));
        when(createAttributeValuePort.persistAll(Set.of(), assessmentResult.getId())).thenReturn(List.of(newAttributeValue));
        when(loadKitCustomLastModificationTimePort.loadLastModificationTime(assessmentResult.getAssessment().getKitCustomId()))
            .thenReturn(assessmentResult.getLastCalculationTime().minusHours(2));

        var result = service.calculateMaturityLevel(param);
        assertNotNull(result);
        assertEquals(levelThree().getValue(), result.maturityLevel().getValue());
        assertTrue(result.resultAffected());

        verify(loadKitLastMajorModificationTimePort, times(1)).loadLastMajorModificationTime(any());
        verify(updateCalculatedResultPort, times(1)).updateCalculatedResult(any(AssessmentResult.class));
        verify(updateAssessmentPort, times(1)).updateLastModificationTime(any(), any());
    }

    @Test
    void testCalculateMaturityLevel_whenCalculationTimeIsNull_thenCreateNewAttributeAndSubjectValuesAndCalculate() {
        List<AttributeValue> s1AttributeValues = List.of(
            hasFullScoreOnLevel24WithWeight(2, 1533),
            hasFullScoreOnLevel24WithWeight(2, 1534),
            hasFullScoreOnLevel23WithWeight(3, 1535),
            hasFullScoreOnLevel23WithWeight(3, 1536)
        );

        List<AttributeValue> s2AttributeValues = List.of(
            hasFullScoreOnLevel24WithWeight(4, 1537),
            hasFullScoreOnLevel23WithWeight(1, 1538)
        );

        List<SubjectValue> subjectValues = List.of(
            withAttributeValues(s1AttributeValues, 5),
            withAttributeValues(s2AttributeValues, 1)
        );

        AssessmentResult assessmentResult = invalidResultWithSubjectValues(subjectValues);
        assessmentResult.setLastCalculationTime(null);
        var param = createParam(b -> b.assessmentId(assessmentResult.getAssessment().getId()));

        List<Subject> subjects = new ArrayList<>(subjectValues.stream().map(SubjectValue::getSubject).toList());
        var newAttributeValue = hasFullScoreOnLevel23WithWeight(4, 1533);
        var newSubjectValue = withAttributeValues(List.of(newAttributeValue), 2);
        subjects.add(newSubjectValue.getSubject());

        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadCalculateInfoPort.load(param.getAssessmentId())).thenReturn(assessmentResult);
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CALCULATE_ASSESSMENT)).thenReturn(true);
        when(loadKitLastMajorModificationTimePort.loadLastMajorModificationTime(any())).thenReturn(LocalDateTime.now());
        when(loadSubjectsPort.loadByKitVersionIdWithAttributes(any())).thenReturn(subjects);
        when(createSubjectValuePort.persistAll(anyList(), any())).thenReturn(List.of(newSubjectValue));
        when(createAttributeValuePort.persistAll(anySet(), any())).thenReturn(List.of(newAttributeValue));
        when(loadKitCustomLastModificationTimePort.loadLastModificationTime(assessmentResult.getAssessment().getKitCustomId())).thenReturn(LocalDateTime.now().minusHours(1));

        var result = service.calculateMaturityLevel(param);

        assertNotNull(result);
        assertNotNull(result.maturityLevel());
        assertTrue(result.resultAffected());

        verify(updateCalculatedResultPort, times(1)).updateCalculatedResult(any(AssessmentResult.class));
        verify(updateAssessmentPort, times(1)).updateLastModificationTime(any(), any());
    }

    private CalculateAssessmentUseCase.Param createParam(Consumer<CalculateAssessmentUseCase.Param.ParamBuilder> changer) {
        var param = paramBuilder();
        changer.accept(param);
        return param.build();
    }

    private CalculateAssessmentUseCase.Param.ParamBuilder paramBuilder() {
        return CalculateAssessmentUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .currentUserId(UUID.randomUUID());
    }
}
