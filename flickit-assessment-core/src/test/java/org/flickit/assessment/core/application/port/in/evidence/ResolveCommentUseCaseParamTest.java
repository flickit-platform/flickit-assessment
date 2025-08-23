package org.flickit.assessment.core.application.port.in.evidence;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.RESOLVE_COMMENT_EVIDENCE_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.*;

class ResolveCommentUseCaseParamTest {

    @Test
    void testResolveCommentUseCaseParam_evidenceIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.id(null)));
        assertThat(throwable).hasMessage("id: " + RESOLVE_COMMENT_EVIDENCE_ID_NOT_NULL);
    }

    @Test
    void testResolveCommentUseCaseParam_currentUserIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<ResolveCommentUseCase.Param.ParamBuilder> changer) {
        var param = paramBuilder();
        changer.accept(param);
        param.build();
    }

    private ResolveCommentUseCase.Param.ParamBuilder paramBuilder() {
        return ResolveCommentUseCase.Param.builder()
            .id(UUID.randomUUID())
            .currentUserId(UUID.randomUUID());
    }
}
