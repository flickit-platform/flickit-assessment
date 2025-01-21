package org.flickit.assessment.core.application.service.attributeinsight;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.port.in.attributeinsight.GetAttributeInsightUseCase;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.attributeinsight.LoadAttributeInsightPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.CREATE_ATTRIBUTE_INSIGHT;
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_SUBJECT_REPORT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_ATTRIBUTE_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.core.test.fixture.application.AssessmentResultMother.validResult;
import static org.flickit.assessment.core.test.fixture.application.AttributeInsightMother.attributeAiInsightWithTimes;
import static org.flickit.assessment.core.test.fixture.application.AttributeInsightMother.attributeInsightWithTimes;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAttributeInsightServiceTest {

    @InjectMocks
    private GetAttributeInsightService service;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private LoadAttributeInsightPort loadAttributeInsightPort;

    @Mock
    private LoadAssessmentResultPort assessmentResultPort;

    private final AssessmentResult assessmentResult = validResult();

    @Test
    void testGetAttributeInsight_whenUserDoesNotHaveRequiredPermission_thenThrowAccessDeniedException() {
        var param = createParam(GetAttributeInsightUseCase.Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_SUBJECT_REPORT)).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.getInsight(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(loadAttributeInsightPort, assessmentResultPort);
    }

    @Test
    void testGetAttributeInsight_whenAssessmentResultDoesNotExist_thenThrowResourceNotFoundException() {
        var param = createParam(GetAttributeInsightUseCase.Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_SUBJECT_REPORT)).thenReturn(true);
        when(assessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.getInsight(param));
        assertEquals(GET_ATTRIBUTE_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND, throwable.getMessage());

        verifyNoInteractions(loadAttributeInsightPort);
    }

    @Test
    void testGetAttributeInsight_whenAttributeInsightDoesNotExistAndUserHasCreateInsightPermission_thenResult() {
        var param = createParam(GetAttributeInsightUseCase.Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_SUBJECT_REPORT)).thenReturn(true);
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ATTRIBUTE_INSIGHT)).thenReturn(true);
        when(assessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadAttributeInsightPort.load(assessmentResult.getId(), param.getAttributeId())).thenReturn(Optional.empty());

        var result = service.getInsight(param);

        assertNotNull(result);
        assertNull(result.aiInsight());
        assertNull(result.assessorInsight());
        assertTrue(result.editable());
        assertNull(result.approved());
    }

    @Test
    void testGetAttributeInsight_whenAttributeInsightDoesNotExistAndUserDoesNotHaveCreateInsightPermission_thenResult() {
        var param = createParam(GetAttributeInsightUseCase.Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_SUBJECT_REPORT)).thenReturn(true);
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ATTRIBUTE_INSIGHT)).thenReturn(false);
        when(assessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadAttributeInsightPort.load(assessmentResult.getId(), param.getAttributeId())).thenReturn(Optional.empty());

        var result = service.getInsight(param);

        assertNotNull(result);
        assertNull(result.aiInsight());
        assertNull(result.assessorInsight());
        assertFalse(result.editable());
        assertNull(result.approved());
    }

    @Test
    void testGetAttributeInsight_whenAssessorInsightIsNotNullAndIsNewerThanAiInsightAndIsValidBasedOnInsightTime_ReturnAssessorInsight() {
        var param = createParam(GetAttributeInsightUseCase.Param.ParamBuilder::build);
        var attributeInsight = attributeInsightWithTimes(
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(1));

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_SUBJECT_REPORT)).thenReturn(true);
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ATTRIBUTE_INSIGHT)).thenReturn(true);
        when(assessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadAttributeInsightPort.load(assessmentResult.getId(), param.getAttributeId())).thenReturn(Optional.of(attributeInsight));

        var result = service.getInsight(param);

        assertNotNull(result);
        assertNull(result.aiInsight());
        assertNotNull(result.assessorInsight());
        assertEquals(attributeInsight.getAssessorInsight(), result.assessorInsight().insight());
        assertEquals(attributeInsight.getAssessorInsightTime(), result.assessorInsight().creationTime());
        assertTrue(result.assessorInsight().isValid());
        assertTrue(result.editable());
        assertTrue(result.approved());
    }

    @Test
    void testGetAttributeInsight_whenAssessorInsightIsNotNullAndIsOlderThanAiInsightAndAiInsightIsValid_thenReturnAiInsight() {
        var param = createParam(GetAttributeInsightUseCase.Param.ParamBuilder::build);
        var attributeInsight = attributeInsightWithTimes(
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(1));

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_SUBJECT_REPORT)).thenReturn(true);
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ATTRIBUTE_INSIGHT)).thenReturn(true);
        when(assessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadAttributeInsightPort.load(assessmentResult.getId(), param.getAttributeId())).thenReturn(Optional.of(attributeInsight));

        var result = service.getInsight(param);
        assertNotNull(result);
        assertNotNull(result.aiInsight());
        assertNull(result.assessorInsight());
        assertEquals(attributeInsight.getAiInsight(), result.aiInsight().insight());
        assertEquals(attributeInsight.getAiInsightTime(), result.aiInsight().creationTime());
        assertTrue(result.aiInsight().isValid());
        assertTrue(result.editable());
        assertTrue(result.approved());
    }

    @Test
    void testGetAttributeInsight_whenAssessorInsightIsNotNullAndIsOlderThanAiInsightAndAiInsightIsNotValid_ReturnAiInsight() {
        var param = createParam(GetAttributeInsightUseCase.Param.ParamBuilder::build);
        var attributeInsight = attributeInsightWithTimes(
            LocalDateTime.now().minusDays(1),
            LocalDateTime.now().minusDays(2),
            LocalDateTime.now().minusDays(1));

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_SUBJECT_REPORT)).thenReturn(true);
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ATTRIBUTE_INSIGHT)).thenReturn(true);
        when(assessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadAttributeInsightPort.load(assessmentResult.getId(), param.getAttributeId())).thenReturn(Optional.of(attributeInsight));

        var result = service.getInsight(param);
        assertNotNull(result);
        assertNotNull(result.aiInsight());
        assertNull(result.assessorInsight());
        assertEquals(attributeInsight.getAiInsight(), result.aiInsight().insight());
        assertEquals(attributeInsight.getAiInsightTime(), result.aiInsight().creationTime());
        assertFalse(result.aiInsight().isValid());
        assertTrue(result.editable());
        assertTrue(result.approved());
    }

    @Test
    void testGetAttributeInsight_whenAssessorInsightIsNotNullAndIsNewerThanAiInsightAndIsNotValid_ReturnAssessorInsight() {
        var param = createParam(GetAttributeInsightUseCase.Param.ParamBuilder::build);
        var attributeInsight = attributeInsightWithTimes(
            LocalDateTime.now().minusDays(2),
            LocalDateTime.now().minusDays(1),
            LocalDateTime.now().minusDays(1));

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_SUBJECT_REPORT)).thenReturn(true);
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ATTRIBUTE_INSIGHT)).thenReturn(true);
        when(assessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadAttributeInsightPort.load(assessmentResult.getId(), param.getAttributeId())).thenReturn(Optional.of(attributeInsight));

        var result = service.getInsight(param);
        assertNotNull(result);
        assertNull(result.aiInsight());
        assertNotNull(result.assessorInsight());
        assertEquals(attributeInsight.getAssessorInsight(), result.assessorInsight().insight());
        assertEquals(attributeInsight.getAssessorInsightTime(), result.assessorInsight().creationTime());
        assertFalse(result.assessorInsight().isValid());
        assertTrue(result.editable());
        assertTrue(result.approved());
    }

    @Test
    void testGetAttributeInsight_whenAssessorInsightIsNull_AiInsightIsNotValid_thenReturnAiInsight() {
        var param = createParam(GetAttributeInsightUseCase.Param.ParamBuilder::build);
        var attributeInsight = attributeAiInsightWithTimes(
            LocalDateTime.now().minusDays(1),
            LocalDateTime.now().minusDays(1));

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_SUBJECT_REPORT)).thenReturn(true);
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ATTRIBUTE_INSIGHT)).thenReturn(true);
        when(assessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadAttributeInsightPort.load(assessmentResult.getId(), param.getAttributeId())).thenReturn(Optional.of(attributeInsight));

        var result = service.getInsight(param);
        assertNotNull(result);
        assertNotNull(result.aiInsight());
        assertEquals(attributeInsight.getAiInsight(), result.aiInsight().insight());
        assertEquals(attributeInsight.getAiInsightTime(), result.aiInsight().creationTime());
        assertFalse(result.aiInsight().isValid());
        assertNull(result.assessorInsight());
        assertTrue(result.editable());
        assertFalse(result.approved());
    }

    @Test
    void testGetAttributeInsight_whenAssessorInsightIsNull_AiInsightIsValidBasedOnInsightTime_thenReturnAiInsight() {
        var param = createParam(GetAttributeInsightUseCase.Param.ParamBuilder::build);
        var attributeInsight = attributeAiInsightWithTimes(
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(1));

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_SUBJECT_REPORT)).thenReturn(true);
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ATTRIBUTE_INSIGHT)).thenReturn(true);
        when(assessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadAttributeInsightPort.load(assessmentResult.getId(), param.getAttributeId())).thenReturn(Optional.of(attributeInsight));

        var result = service.getInsight(param);
        assertNotNull(result);
        assertNotNull(result.aiInsight());
        assertEquals(attributeInsight.getAiInsight(), result.aiInsight().insight());
        assertEquals(attributeInsight.getAiInsightTime(), result.aiInsight().creationTime());
        assertTrue(result.aiInsight().isValid());
        assertNull(result.assessorInsight());
        assertTrue(result.editable());
        assertFalse(result.approved());
    }

    @Test
    void testGetAttributeInsight_whenAssessorInsightIsNull_AiInsightIsValidBasedOnLastModificationTime_thenReturnAiInsight() {
        var param = createParam(GetAttributeInsightUseCase.Param.ParamBuilder::build);
        var attributeInsight = attributeAiInsightWithTimes(
            LocalDateTime.now().minusDays(1),
            LocalDateTime.now().plusDays(1));

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_SUBJECT_REPORT)).thenReturn(true);
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ATTRIBUTE_INSIGHT)).thenReturn(true);
        when(assessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadAttributeInsightPort.load(assessmentResult.getId(), param.getAttributeId())).thenReturn(Optional.of(attributeInsight));

        var result = service.getInsight(param);
        assertNotNull(result);
        assertNotNull(result.aiInsight());
        assertEquals(attributeInsight.getAiInsight(), result.aiInsight().insight());
        assertEquals(attributeInsight.getAiInsightTime(), result.aiInsight().creationTime());
        assertTrue(result.aiInsight().isValid());
        assertNull(result.assessorInsight());
        assertTrue(result.editable());
        assertFalse(result.approved());
    }

    private GetAttributeInsightUseCase.Param createParam(Consumer<GetAttributeInsightUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private GetAttributeInsightUseCase.Param.ParamBuilder paramBuilder() {
        return GetAttributeInsightUseCase.Param.builder()
            .assessmentId(assessmentResult.getAssessment().getId())
            .attributeId(123L)
            .currentUserId(UUID.randomUUID());
    }
}
