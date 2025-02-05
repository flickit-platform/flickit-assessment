package org.flickit.assessment.core.application.service.assessmentinsight;

import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AssessmentInsight;
import org.flickit.assessment.core.application.port.in.assessmentinsight.InitAssessmentInsightUseCase;
import org.flickit.assessment.core.application.port.out.assessment.GetAssessmentProgressPort;
import org.flickit.assessment.core.application.port.out.assessmentinsight.CreateAssessmentInsightPort;
import org.flickit.assessment.core.application.port.out.assessmentinsight.LoadAssessmentInsightPort;
import org.flickit.assessment.core.application.port.out.assessmentinsight.UpdateAssessmentInsightPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.test.fixture.application.AssessmentResultMother;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_ASSESSMENT_REPORT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.INIT_ASSESSMENT_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.core.common.MessageKey.ASSESSMENT_DEFAULT_INSIGHT_DEFAULT_COMPLETED;
import static org.flickit.assessment.core.common.MessageKey.ASSESSMENT_DEFAULT_INSIGHT_DEFAULT_INCOMPLETE;
import static org.flickit.assessment.core.test.fixture.application.AssessmentInsightMother.createDefaultInsightWithAssessmentResultId;
import static org.flickit.assessment.core.test.fixture.application.AssessmentResultMother.validResult;
import static org.flickit.assessment.core.test.fixture.application.MaturityLevelMother.levelFive;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InitAssessmentInsightServiceTest {

    @InjectMocks
    private InitAssessmentInsightService service;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private LoadAssessmentResultPort loadAssessmentResultPort;

    @Mock
    private LoadAssessmentInsightPort loadAssessmentInsightPort;

    @Mock
    private GetAssessmentProgressPort getAssessmentProgressPort;

    @Mock
    private CreateAssessmentInsightPort createAssessmentInsightPort;

    @Mock
    private UpdateAssessmentInsightPort updateAssessmentInsightPort;

    @Mock
    private ValidateAssessmentResultPort validateAssessmentResultPort;

    @Test
    void testInitAssessmentInsight_whenCurrentUserDoesNotHaveRequiredPermission_thenThrowAccessDeniedException() {
        var param = createParam(InitAssessmentInsightUseCase.Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT))
            .thenReturn(false);

        var throwable = Assertions.assertThrows(AccessDeniedException.class, () -> service.initAssessmentInsight(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(loadAssessmentResultPort,
            createAssessmentInsightPort,
            updateAssessmentInsightPort,
            validateAssessmentResultPort,
            loadAssessmentInsightPort);
    }

    @Test
    void testInitAssessmentInsight_whenAssessmentResultNotFound_thenThrowResourceNotFoundException() {
        var param = createParam(InitAssessmentInsightUseCase.Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.initAssessmentInsight(param));
        assertEquals(INIT_ASSESSMENT_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND, throwable.getMessage());

        verifyNoInteractions(loadAssessmentInsightPort,
            getAssessmentProgressPort,
            createAssessmentInsightPort,
            updateAssessmentInsightPort,
            validateAssessmentResultPort);
    }

    @Test
    void testInitAssessmentInsight_whenInsightNotExistsAndAssessmentIsComplete_thenCrateAssessmentInsight() {
        var param = createParam(InitAssessmentInsightUseCase.Param.ParamBuilder::build);
        var assessmentResult = validResult();
        var progressResult = new GetAssessmentProgressPort.Result(UUID.randomUUID(), 15, 15);
        var expectedDefaultInsight = MessageBundle.message(ASSESSMENT_DEFAULT_INSIGHT_DEFAULT_COMPLETED,
            assessmentResult.getMaturityLevel().getTitle(),
            progressResult.questionsCount(),
            Math.ceil(assessmentResult.getConfidenceValue()));

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        doNothing().when(validateAssessmentResultPort).validate(param.getAssessmentId());
        when(loadAssessmentInsightPort.loadByAssessmentResultId(assessmentResult.getId())).thenReturn(Optional.empty());
        when(getAssessmentProgressPort.getProgress(assessmentResult.getAssessment().getId())).thenReturn(progressResult);

        service.initAssessmentInsight(param);

        ArgumentCaptor<AssessmentInsight> createCaptor = ArgumentCaptor.forClass(AssessmentInsight.class);
        verify(createAssessmentInsightPort).persist(createCaptor.capture());
        assertNull(createCaptor.getValue().getId());
        assertEquals(assessmentResult.getId(), createCaptor.getValue().getAssessmentResultId());
        assertNull(createCaptor.getValue().getInsightBy());
        assertNotNull(createCaptor.getValue().getInsightTime());
        assertNotNull(createCaptor.getValue().getLastModificationTime());
        assertEquals(expectedDefaultInsight, createCaptor.getValue().getInsight());
        assertFalse(createCaptor.getValue().isApproved());

        verifyNoInteractions(updateAssessmentInsightPort);
    }

    @Test
    void testInitAssessmentInsight_whenInsightExistsAndAssessmentIsIncompleteWithNullConfidenceValue_thenUpdateInsight() {
        var param = createParam(InitAssessmentInsightUseCase.Param.ParamBuilder::build);
        var assessmentResult = AssessmentResultMother.validResultWithSubjectValuesAndMaturityLevel(null, levelFive());
        var assessmentInsight = createDefaultInsightWithAssessmentResultId(assessmentResult.getId());
        var progressResult = new GetAssessmentProgressPort.Result(UUID.randomUUID(), 0, 15);
        var expectedInsight = MessageBundle.message(ASSESSMENT_DEFAULT_INSIGHT_DEFAULT_INCOMPLETE,
            assessmentResult.getMaturityLevel().getTitle(),
            progressResult.answersCount(),
            progressResult.questionsCount(),
            0);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        doNothing().when(validateAssessmentResultPort).validate(param.getAssessmentId());
        when(loadAssessmentInsightPort.loadByAssessmentResultId(assessmentResult.getId())).thenReturn(Optional.of(assessmentInsight));
        when(getAssessmentProgressPort.getProgress(assessmentResult.getAssessment().getId())).thenReturn(progressResult);

        service.initAssessmentInsight(param);

        ArgumentCaptor<AssessmentInsight> createCaptor = ArgumentCaptor.forClass(AssessmentInsight.class);
        verify(updateAssessmentInsightPort).updateInsight(createCaptor.capture());
        assertEquals(assessmentInsight.getId(), createCaptor.getValue().getId());
        assertEquals(assessmentResult.getId(), createCaptor.getValue().getAssessmentResultId());
        assertNull(createCaptor.getValue().getInsightBy());
        assertNotNull(createCaptor.getValue().getInsightTime());
        assertNotNull(createCaptor.getValue().getLastModificationTime());
        assertEquals(expectedInsight, createCaptor.getValue().getInsight());
        assertFalse(createCaptor.getValue().isApproved());

        verifyNoInteractions(createAssessmentInsightPort);
    }

    private InitAssessmentInsightUseCase.Param createParam(Consumer<InitAssessmentInsightUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private InitAssessmentInsightUseCase.Param.ParamBuilder paramBuilder() {
        return InitAssessmentInsightUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .currentUserId(UUID.randomUUID());
    }
}
