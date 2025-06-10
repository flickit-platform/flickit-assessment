package org.flickit.assessment.advice.application.service.advice;

import org.flickit.assessment.advice.application.domain.AttributeLevelTarget;
import org.flickit.assessment.advice.application.port.in.advice.CreateAdviceUseCase;
import org.flickit.assessment.advice.test.fixture.application.AdviceListItemMother;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.exception.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.CREATE_ADVICE;
import static org.flickit.assessment.common.error.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateAdviceServiceTest {

    @InjectMocks
    private CreateAdviceService service;

    @Mock
    private ValidateAssessmentResultPort validateAssessmentResultPort;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private CreateAdviceHelper createAdviceHelper;

    private final CreateAdviceUseCase.Param param = createParam(CreateAdviceUseCase.Param.ParamBuilder::build);

    @Test
    void testCreateAdvice_whenCurrentUserDoesNotHaveRequiredPermission_thenThrowAccessDeniedException() {
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ADVICE))
            .thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.createAdvice(param), COMMON_CURRENT_USER_NOT_ALLOWED);
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verify(assessmentAccessChecker, times(1)).isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ADVICE);
        verifyNoInteractions(createAdviceHelper);
    }

    @Test
    void testCreateAdvice_whenValidParam_thenReturnsAdvice() {
        var adviceItems = List.of(AdviceListItemMother.createSimpleAdviceListItem(), AdviceListItemMother.createSimpleAdviceListItem());

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ADVICE))
            .thenReturn(true);
        when(createAdviceHelper.createAdvice(param.getAssessmentId(), param.getAttributeLevelTargets())).thenReturn(adviceItems);

        var result = service.createAdvice(param);

        assertThat(result.adviceItems())
            .allSatisfy(question -> {
                assertThat(question.recommendedOption()).isNotNull();
                assertThat(question.attributes()).isNotNull();
                assertThat(question.questionnaire()).isNotNull();
                assertThat(question.question().title()).isNotBlank();
                assertThat(question.benefit()).isNotZero();
            });

        verify(validateAssessmentResultPort, times(1)).validate(param.getAssessmentId());
        verify(assessmentAccessChecker, times(1)).isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ADVICE);
    }

    private CreateAdviceUseCase.Param createParam(Consumer<CreateAdviceUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private CreateAdviceUseCase.Param.ParamBuilder paramBuilder() {
        return CreateAdviceUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .attributeLevelTargets(List.of(new AttributeLevelTarget(1L, 2L)))
            .currentUserId(UUID.randomUUID());
    }
}
