package org.flickit.assessment.core.application.service.evidence;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.port.in.evidence.ApproveAssessmentAnswersUseCase;
import org.flickit.assessment.core.application.port.out.answer.ApproveAnswerPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.APPROVE_ALL_ANSWERS;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApproveAssessmentAnswersServiceTest {

    @InjectMocks
    private ApproveAssessmentAnswersService service;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private ApproveAnswerPort approveAnswerPort;


    private ApproveAssessmentAnswersUseCase.Param param = createParam(ApproveAssessmentAnswersUseCase.Param.ParamBuilder::build);

    @Test
    void testApproveAllAnswers_whenUserDoesNotHaveRequiredPermission_thenThrowAccessDeniedException() {
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), APPROVE_ALL_ANSWERS))
            .thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.approveAllAnswers(param));

        assertThat(throwable.getMessage()).isEqualTo(COMMON_CURRENT_USER_ID_NOT_NULL);

        verifyNoInteractions(approveAnswerPort);
    }

    @Test
    void testApproveAllAnswers_whenParametersAreValid_thenSuccessfullyApproved() {
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), APPROVE_ALL_ANSWERS))
            .thenReturn(true);

        service.approveAllAnswers(param);

        verify(approveAnswerPort).approveAll(param.getAssessmentId(), param.getCurrentUserId());
    }

    private ApproveAssessmentAnswersUseCase.Param createParam(Consumer<ApproveAssessmentAnswersUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private ApproveAssessmentAnswersUseCase.Param.ParamBuilder paramBuilder() {
        return ApproveAssessmentAnswersUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .currentUserId(UUID.randomUUID());
    }
}
