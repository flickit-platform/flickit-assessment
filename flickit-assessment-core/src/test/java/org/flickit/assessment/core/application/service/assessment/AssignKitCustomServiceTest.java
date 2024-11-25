package org.flickit.assessment.core.application.service.assessment;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.port.in.assessment.AssignKitCustomUseCase;
import org.flickit.assessment.core.application.port.out.assessment.UpdateAssessmentPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.MANAGE_KIT_CUSTOM;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssignKitCustomServiceTest {

    @InjectMocks
    private AssignKitCustomService service;

    @Mock
    private UpdateAssessmentPort updateAssessmentPort;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Test
    void testAssignKitCustom_WhenCurrentUserDoesNotHaveRequiredPermission_ThenThrowAccessDeniedException() {
        var param = createParam(AssignKitCustomUseCase.Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), MANAGE_KIT_CUSTOM)).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.assignKitCustom(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(updateAssessmentPort);
    }

    @Test
    void testAssignKitCustom_WhenCurrentUserHasRequiredPermission_ThenUpdateAssessmentKitCustom() {
        var param = createParam(AssignKitCustomUseCase.Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), MANAGE_KIT_CUSTOM)).thenReturn(true);
        doNothing().when(updateAssessmentPort).updateKitCustomId(param.getAssessmentId(), param.getKitCustomId());

        service.assignKitCustom(param);

        verify(updateAssessmentPort).updateKitCustomId(param.getAssessmentId(), param.getKitCustomId());
    }

    private AssignKitCustomUseCase.Param createParam(Consumer<AssignKitCustomUseCase.Param.ParamBuilder> changer) {
        var param = paramBuilder();
        changer.accept(param);
        return param.build();
    }

    private AssignKitCustomUseCase.Param.ParamBuilder paramBuilder() {
        return AssignKitCustomUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .kitCustomId(1L)
            .currentUserId(UUID.randomUUID());
    }
}
