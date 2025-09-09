package org.flickit.assessment.core.application.port.in.assessmentuserrole;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DeleteAssessmentUserRoleUseCaseParamTest {

    @Test
    void testDeleteAssessmentUserRoleParam_assessmentIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.assessmentId(null)));
        assertThat(throwable).hasMessage("assessmentId: " + DELETE_ASSESSMENT_USER_ROLE_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void testDeleteAssessmentUserRoleParam_userIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.userId(null)));
        assertThat(throwable).hasMessage("userId: " + DELETE_ASSESSMENT_USER_ROLE_USER_ID_NOT_NULL);
    }

    @Test
    void testDeleteAssessmentUserRoleParam_currentUserIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<DeleteUserAssessmentRoleUseCase.Param.ParamBuilder> changer) {
        var param = paramBuilder();
        changer.accept(param);
        param.build();
    }

    private DeleteUserAssessmentRoleUseCase.Param.ParamBuilder paramBuilder() {
        return DeleteUserAssessmentRoleUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .userId(UUID.randomUUID())
            .currentUserId(UUID.randomUUID());
    }
}
