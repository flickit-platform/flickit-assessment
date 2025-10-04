package org.flickit.assessment.core.application.port.in.assessmentinvite;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.DELETE_ASSESSMENT_INVITE_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DeleteAssessmentInviteUseCaseParamTest {

    @Test
    void testDeleteAssessmentInviteParam_idIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.id(null)));
        assertThat(throwable).hasMessage("id: " + DELETE_ASSESSMENT_INVITE_ID_NOT_NULL);
    }

    @Test
    void testDeleteAssessmentInviteParam_currentUserIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<DeleteAssessmentInviteUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private DeleteAssessmentInviteUseCase.Param.ParamBuilder paramBuilder() {
        return DeleteAssessmentInviteUseCase.Param.builder()
            .id(UUID.randomUUID())
            .currentUserId(UUID.randomUUID());
    }
}
