package org.flickit.assessment.kit.application.port.in.questionnaire;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.*;

class GetQuestionnairesUseCaseParamTest {

    @Test
    void testGetQuestionnairesUseCaseParam_kitVersionIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.kitVersionId(null)));
        assertThat(throwable).hasMessage("kitVersionId: " + GET_QUESTIONNAIRES_KIT_VERSION_ID_NOT_NULL);
    }

    @Test
    void testGetQuestionnairesUseCaseParam_currentUserIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    @Test
    void testGetQuestionnairesUseCaseParam_pageParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.page(-1)));
        assertThat(throwable).hasMessage("page: " + GET_QUESTIONNAIRES_PAGE_MIN);
    }

    @Test
    void testGetQuestionnairesUseCaseParam_sizeParamViolatesConstraints_ConstraintViolationException() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.size(0)));
        assertThat(throwable).hasMessage("size: " + GET_QUESTIONNAIRES_SIZE_MIN);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.size(101)));
        assertThat(throwable).hasMessage("size: " + GET_QUESTIONNAIRES_SIZE_MAX);
    }

    private void createParam(Consumer<GetQuestionnairesUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private GetQuestionnairesUseCase.Param.ParamBuilder paramBuilder() {
        return GetQuestionnairesUseCase.Param.builder()
            .kitVersionId(1L)
            .currentUserId(UUID.randomUUID())
            .page(1)
            .size(10);
    }
}
