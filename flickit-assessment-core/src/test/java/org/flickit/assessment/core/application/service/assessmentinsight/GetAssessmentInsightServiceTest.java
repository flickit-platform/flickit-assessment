package org.flickit.assessment.core.application.service.assessmentinsight;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.port.in.assessmentinsight.GetAssessmentInsightUseCase;
import org.flickit.assessment.core.application.port.in.assessmentinsight.GetAssessmentInsightUseCase.Param;
import org.flickit.assessment.core.application.port.out.assessmentinsight.LoadAssessmentInsightPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.*;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_ASSESSMENT_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.core.test.fixture.application.AssessmentInsightMother.*;
import static org.flickit.assessment.core.test.fixture.application.AssessmentResultMother.validResult;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAssessmentInsightServiceTest {

    @InjectMocks
    private GetAssessmentInsightService service;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private LoadAssessmentResultPort loadAssessmentResultPort;

    @Mock
    ValidateAssessmentResultPort validateAssessmentResultPort;

    @Mock
    private LoadAssessmentInsightPort loadAssessmentInsightPort;

    @Test
    void testGetAssessmentInsight_whenCurrentUserDoesNotHaveRequiredPermission_thenThrowAccessDeniedException() {
        var param = createParam(Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT)).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.getAssessmentInsight(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verify(assessmentAccessChecker).isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT);
        verifyNoInteractions(loadAssessmentResultPort, validateAssessmentResultPort, loadAssessmentInsightPort);
    }

    @Test
    void testGetAssessmentInsight_whenAssessmentResultNotFound_thenThrowResourceNotFoundException() {
        var param = createParam(Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.getAssessmentInsight(param));
        assertEquals(GET_ASSESSMENT_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND, throwable.getMessage());

        verifyNoInteractions(validateAssessmentResultPort, loadAssessmentInsightPort);
    }

    @Test
    void testGetAssessmentInsight_whenAssessmentInsightDoesNotExist_thenReturnEmpty() {
        var param = createParam(Param.ParamBuilder::build);
        var assessmentResult = validResult();

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        doNothing().when(validateAssessmentResultPort).validate(param.getAssessmentId());
        when(loadAssessmentInsightPort.loadByAssessmentResultId(assessmentResult.getId())).thenReturn(Optional.empty());
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ASSESSMENT_INSIGHT)).thenReturn(true);

        var result = service.getAssessmentInsight(param);

        assertNotNull(result);
        assertNull(result.defaultInsight());
        assertNull(result.assessorInsight());
        assertTrue(result.editable());
        assertFalse(result.approved());
    }

    @Test
    void testGetAssessmentInsight_whenAssessmentInsightExistsAndIsValidAndEditable_thenReturnAssessorInsight() {
        var param = createParam(Param.ParamBuilder::build);
        var assessmentResult = validResult();
        var assessmentInsight = createWithAssessmentResultId(assessmentResult.getId());

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        doNothing().when(validateAssessmentResultPort).validate(param.getAssessmentId());
        when(loadAssessmentInsightPort.loadByAssessmentResultId(assessmentResult.getId())).thenReturn(Optional.of(assessmentInsight));
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ASSESSMENT_INSIGHT)).thenReturn(true);

        var result = service.getAssessmentInsight(param);

        assertNull(result.defaultInsight());
        assertNotNull(result.assessorInsight());
        assertEquals(assessmentInsight.getInsight(), result.assessorInsight().insight());
        assertEquals(assessmentInsight.getInsightTime(), result.assessorInsight().creationTime());
        assertTrue(result.assessorInsight().isValid());
        assertTrue(result.editable());
    }

    @Test
    void testGetAssessmentInsight_whenAssessmentInsightExistsAndIsNotValidAndNotEditable_thenReturnAssessorInsight() {
        var param = createParam(Param.ParamBuilder::build);
        var assessmentResult = validResult();
        var assessmentInsight = createWithMinInsightTime();

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        doNothing().when(validateAssessmentResultPort).validate(param.getAssessmentId());
        when(loadAssessmentInsightPort.loadByAssessmentResultId(assessmentResult.getId())).thenReturn(Optional.of(assessmentInsight));
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ASSESSMENT_INSIGHT)).thenReturn(false);

        var result = service.getAssessmentInsight(param);

        assertNotNull(result.assessorInsight());
        assertEquals(assessmentInsight.getInsight(), result.assessorInsight().insight());
        assertEquals(assessmentInsight.getInsightTime(), result.assessorInsight().creationTime());
        assertFalse(result.assessorInsight().isValid());
        assertNull(result.defaultInsight());
        assertFalse(result.editable());
        assertFalse(result.approved());
    }

    @Test
    void testGetAssessmentInsight_whenDefaultInsightExistsAndIsValidAndEditable_ReturnDefaultInsight() {
        var param = createParam(Param.ParamBuilder::build);
        var assessmentResult = validResult();
        var assessmentInsight = createDefaultInsightWithAssessmentResultId(assessmentResult.getId());

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        doNothing().when(validateAssessmentResultPort).validate(param.getAssessmentId());
        when(loadAssessmentInsightPort.loadByAssessmentResultId(assessmentResult.getId())).thenReturn(Optional.of(assessmentInsight));
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ASSESSMENT_INSIGHT)).thenReturn(true);

        var result = service.getAssessmentInsight(param);

        assertNotNull(result.defaultInsight().insight());
        assertEquals(assessmentInsight.getInsight(), result.defaultInsight().insight());
        assertEquals(assessmentInsight.getInsightTime(), result.defaultInsight().creationTime());
        assertTrue(result.defaultInsight().isValid());
        assertNull(result.assessorInsight());
        assertTrue(result.editable());
        assertFalse(result.approved());
    }

    @Test
    void testGetAssessmentInsight_whenDefaultInsightExistsAndIsNotValidAndNotEditable_ReturnDefaultInsight() {
        var param = createParam(Param.ParamBuilder::build);
        var assessmentResult = validResult();
        var lastCalculateTime = assessmentResult.getLastCalculationTime();
        var assessmentInsight = createDefaultInsightWithTimesAndApprove(lastCalculateTime.minusDays(1),
            lastCalculateTime.minusDays(1),
            true);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        doNothing().when(validateAssessmentResultPort).validate(param.getAssessmentId());
        when(loadAssessmentInsightPort.loadByAssessmentResultId(assessmentResult.getId())).thenReturn(Optional.of(assessmentInsight));
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ASSESSMENT_INSIGHT)).thenReturn(false);

        var result = service.getAssessmentInsight(param);

        assertNotNull(result.defaultInsight().insight());
        assertEquals(assessmentInsight.getInsight(), result.defaultInsight().insight());
        assertEquals(assessmentInsight.getInsightTime(), result.defaultInsight().creationTime());
        assertFalse(result.defaultInsight().isValid());
        assertNull(result.assessorInsight());
        assertFalse(result.editable());
        assertTrue(result.approved());
    }

    private Param createParam(Consumer<Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private GetAssessmentInsightUseCase.Param.ParamBuilder paramBuilder() {
        return GetAssessmentInsightUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .currentUserId(UUID.randomUUID());
    }
}
