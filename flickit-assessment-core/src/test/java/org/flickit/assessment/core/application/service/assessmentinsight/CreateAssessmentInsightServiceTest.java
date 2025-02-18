package org.flickit.assessment.core.application.service.assessmentinsight;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AssessmentInsight;
import org.flickit.assessment.core.application.port.in.assessmentinsight.CreateAssessmentInsightUseCase;
import org.flickit.assessment.core.application.port.in.assessmentinsight.CreateAssessmentInsightUseCase.Param;
import org.flickit.assessment.core.application.port.out.assessmentinsight.CreateAssessmentInsightPort;
import org.flickit.assessment.core.application.port.out.assessmentinsight.LoadAssessmentInsightPort;
import org.flickit.assessment.core.application.port.out.assessmentinsight.UpdateAssessmentInsightPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.test.fixture.application.AssessmentInsightMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.CREATE_ASSESSMENT_INSIGHT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.CREATE_ASSESSMENT_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.core.test.fixture.application.AssessmentResultMother.validResult;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateAssessmentInsightServiceTest {

    @InjectMocks
    private CreateAssessmentInsightService service;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private LoadAssessmentResultPort loadAssessmentResultPort;

    @Mock
    LoadAssessmentInsightPort loadAssessmentInsightPort;

    @Mock
    private CreateAssessmentInsightPort createAssessmentInsightPort;

    @Mock
    private UpdateAssessmentInsightPort updateAssessmentInsightPort;

    @Test
    void testCreateAssessmentInsight_whenCurrentUserDoesNotHaveRequiredPermission_thenThrowAccessDeniedException() {
        var param = createParam(Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ASSESSMENT_INSIGHT))
            .thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.createAssessmentInsight(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verify(assessmentAccessChecker).isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ASSESSMENT_INSIGHT);
    }

    @Test
    void testCreateAssessmentInsight_whenNoAssessmentResultExists_thenThrowResourceNotFoundException() {
        var param = createParam(Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ASSESSMENT_INSIGHT))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.createAssessmentInsight(param));
        assertEquals(CREATE_ASSESSMENT_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND, throwable.getMessage());

        verify(assessmentAccessChecker).isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ASSESSMENT_INSIGHT);
        verify(loadAssessmentResultPort).loadByAssessmentId(param.getAssessmentId());
    }

    @Test
    void testCreateAssessmentInsight_whenNoAssessmentInsightFound_thenCreateAssessmentInsight() {
        var param = createParam(Param.ParamBuilder::build);
        var assessmentResult = validResult();
        var assessmentInsightId = UUID.randomUUID();

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ASSESSMENT_INSIGHT)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadAssessmentInsightPort.loadByAssessmentResultId(assessmentResult.getId())).thenReturn(Optional.empty());
        when(createAssessmentInsightPort.persist(any(AssessmentInsight.class))).thenReturn(assessmentInsightId);

        service.createAssessmentInsight(param);

        verify(assessmentAccessChecker).isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ASSESSMENT_INSIGHT);
        verify(loadAssessmentResultPort).loadByAssessmentId(param.getAssessmentId());
        verify(loadAssessmentInsightPort).loadByAssessmentResultId(assessmentResult.getId());

        ArgumentCaptor<AssessmentInsight> createCaptor = ArgumentCaptor.forClass(AssessmentInsight.class);
        verify(createAssessmentInsightPort).persist(createCaptor.capture());
        assertNull(createCaptor.getValue().getId());
        assertEquals(assessmentResult.getId(), createCaptor.getValue().getAssessmentResultId());
        assertEquals(param.getCurrentUserId(), createCaptor.getValue().getInsightBy());
        assertNotNull(createCaptor.getValue().getInsightTime());
        assertNotNull(createCaptor.getValue().getLastModificationTime());
        assertEquals(param.getInsight(), createCaptor.getValue().getInsight());
        assertTrue(createCaptor.getValue().isApproved());

        verifyNoInteractions(updateAssessmentInsightPort);
    }

    @Test
    void testCreateAssessmentInsight_whenAssessmentInsightFound_thenUpdateAssessmentInsight() {
        var assessmentResult = validResult();
        var assessmentInsight = AssessmentInsightMother.createWithAssessmentResultId(assessmentResult.getId());
        var param = createParam(b -> b.assessmentId(assessmentResult.getAssessment().getId()));

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ASSESSMENT_INSIGHT)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadAssessmentInsightPort.loadByAssessmentResultId(assessmentResult.getId())).thenReturn(Optional.of(assessmentInsight));
        doNothing().when(updateAssessmentInsightPort).updateInsight(any(AssessmentInsight.class));

        service.createAssessmentInsight(param);

        verify(assessmentAccessChecker).isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ASSESSMENT_INSIGHT);
        verify(loadAssessmentResultPort).loadByAssessmentId(param.getAssessmentId());
        verify(updateAssessmentInsightPort).updateInsight(isA(AssessmentInsight.class));

        ArgumentCaptor<AssessmentInsight> createCaptor = ArgumentCaptor.forClass(AssessmentInsight.class);
        verify(updateAssessmentInsightPort).updateInsight(createCaptor.capture());
        assertEquals(assessmentInsight.getId(), createCaptor.getValue().getId());
        assertEquals(assessmentResult.getId(), createCaptor.getValue().getAssessmentResultId());
        assertEquals(param.getCurrentUserId(), createCaptor.getValue().getInsightBy());
        assertNotNull(createCaptor.getValue().getInsightTime());
        assertNotNull(createCaptor.getValue().getLastModificationTime());
        assertEquals(param.getInsight(), createCaptor.getValue().getInsight());
        assertTrue(createCaptor.getValue().isApproved());

        verifyNoInteractions(createAssessmentInsightPort);
    }

    private CreateAssessmentInsightUseCase.Param createParam(Consumer<CreateAssessmentInsightUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private CreateAssessmentInsightUseCase.Param.ParamBuilder paramBuilder() {
        return CreateAssessmentInsightUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .insight("insight")
            .currentUserId(UUID.randomUUID());
    }
}
