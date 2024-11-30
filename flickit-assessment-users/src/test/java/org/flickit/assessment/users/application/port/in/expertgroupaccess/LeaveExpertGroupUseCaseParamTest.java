package org.flickit.assessment.users.application.port.in.expertgroupaccess;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.users.common.ErrorMessageKey.LEAVE_EXPERT_GROUP_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LeaveExpertGroupUseCaseParamTest {

    @Test
    void testLeaveExpertGroupUseCaseParam_expertGroupIdViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.expertGroupId(null)));
        assertThat(throwable).hasMessage("expertGroupId: " + LEAVE_EXPERT_GROUP_ID_NOT_NULL);
    }

    @Test
    void testLeaveExpertGroupUseCaseParam_currentUserIdViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<LeaveExpertGroupUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private LeaveExpertGroupUseCase.Param.ParamBuilder paramBuilder() {
        return LeaveExpertGroupUseCase.Param.builder()
            .expertGroupId(123L)
            .currentUserId(UUID.randomUUID());
    }
}
