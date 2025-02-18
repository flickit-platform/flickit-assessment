package org.flickit.assessment.advice.application.service.advicenarration;

import org.apache.commons.lang3.RandomStringUtils;
import org.flickit.assessment.advice.application.domain.AdviceNarration;
import org.flickit.assessment.advice.application.domain.AssessmentResult;
import org.flickit.assessment.advice.application.port.in.advicenarration.CreateAssessorAdviceNarrationUseCase;
import org.flickit.assessment.advice.application.port.out.advicenarration.CreateAdviceNarrationPort;
import org.flickit.assessment.advice.application.port.out.advicenarration.LoadAdviceNarrationPort;
import org.flickit.assessment.advice.application.port.out.advicenarration.UpdateAdviceNarrationPort;
import org.flickit.assessment.advice.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.advice.test.fixture.application.AssessmentResultMother;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.advice.common.ErrorMessageKey.CREATE_ASSESSOR_ADVICE_NARRATION_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.CREATE_ADVICE;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateAssessorAdviceNarrationServiceTest {

    @InjectMocks
    private CreateAssessorAdviceNarrationService service;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private LoadAssessmentResultPort loadAssessmentResultPort;

    @Mock
    private LoadAdviceNarrationPort loadAdviceNarrationPort;

    @Mock
    private ValidateAssessmentResultPort validateAssessmentResultPort;

    @Mock
    private CreateAdviceNarrationPort createAdviceNarrationPort;

    @Mock
    private UpdateAdviceNarrationPort updateAdviceNarrationPort;

    @Test
    void testCreateAssessorAdviceNarration_WhenCurrentUserDoesNotHaveRequiredPermission_ThenThrowAccessDeniedException() {
        var param = createParam(CreateAssessorAdviceNarrationUseCase.Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ADVICE)).thenReturn(false);

        var accessDeniedException = assertThrows(AccessDeniedException.class, () -> service.createAssessorAdviceNarration(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, accessDeniedException.getMessage());
    }

    @Test
    void testCreateAssessorAdviceNarration_WhenAssessmentResultDoesNotNotExist_ThenThrowResourceNotFoundException() {
        var param = createParam(CreateAssessorAdviceNarrationUseCase.Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ADVICE)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.empty());

        var resourceNotFoundException = assertThrows(ResourceNotFoundException.class, () -> service.createAssessorAdviceNarration(param));
        assertEquals(CREATE_ASSESSOR_ADVICE_NARRATION_ASSESSMENT_RESULT_NOT_FOUND, resourceNotFoundException.getMessage());
    }

    @Test
    void testCreateAssessorAdviceNarration_WhenAdviceNarrationDoesNotExist_ThenCreateNewOne() {
        var param = createParam(CreateAssessorAdviceNarrationUseCase.Param.ParamBuilder::build);
        AssessmentResult assessmentResult = AssessmentResultMother.createAssessmentResult();

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ADVICE)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        doNothing().when(validateAssessmentResultPort).validate(param.getAssessmentId());
        when(loadAdviceNarrationPort.loadByAssessmentResultId(assessmentResult.getId())).thenReturn(Optional.empty());
        doNothing().when(createAdviceNarrationPort).persist(any(AdviceNarration.class));

        service.createAssessorAdviceNarration(param);

        verifyNoInteractions(updateAdviceNarrationPort);
    }

    @Test
    void testCreateAssessorAdviceNarration_WhenAdviceExists_ThenUpdateItsAssessorNarration() {
        var param = createParam(CreateAssessorAdviceNarrationUseCase.Param.ParamBuilder::build);
        UUID assessmentResultId = UUID.randomUUID();
        AssessmentResult assessmentResult = AssessmentResultMother.createAssessmentResult();
        AdviceNarration adviceNarration = new AdviceNarration(UUID.randomUUID(),
            assessmentResultId,
            "aiNarration",
            null,
            LocalDateTime.now(),
            null,
            param.getCurrentUserId());

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ADVICE)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        doNothing().when(validateAssessmentResultPort).validate(param.getAssessmentId());
        when(loadAdviceNarrationPort.loadByAssessmentResultId(assessmentResult.getId())).thenReturn(Optional.of(adviceNarration));
        doNothing().when(updateAdviceNarrationPort).updateAssessorNarration(any(UpdateAdviceNarrationPort.AssessorNarrationParam.class));

        service.createAssessorAdviceNarration(param);

        ArgumentCaptor<UpdateAdviceNarrationPort.AssessorNarrationParam> updateParamCaptor = ArgumentCaptor.forClass(UpdateAdviceNarrationPort.AssessorNarrationParam.class);
        verify(updateAdviceNarrationPort).updateAssessorNarration(updateParamCaptor.capture());
        assertEquals(adviceNarration.getId(), updateParamCaptor.getValue().id());
        assertEquals(param.getAssessorNarration(), updateParamCaptor.getValue().narration());
        assertNotNull(updateParamCaptor.getValue().id());
        assertEquals(param.getCurrentUserId(), updateParamCaptor.getValue().createdBy());

        verifyNoInteractions(createAdviceNarrationPort);
    }

    private CreateAssessorAdviceNarrationUseCase.Param createParam(Consumer<CreateAssessorAdviceNarrationUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private CreateAssessorAdviceNarrationUseCase.Param.ParamBuilder paramBuilder() {
        return CreateAssessorAdviceNarrationUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .assessorNarration(RandomStringUtils.randomAlphabetic(100))
            .currentUserId(UUID.randomUUID());
    }
}
