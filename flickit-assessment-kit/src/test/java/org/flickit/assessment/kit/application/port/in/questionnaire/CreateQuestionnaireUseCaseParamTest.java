package org.flickit.assessment.kit.application.port.in.questionnaire;

import jakarta.validation.ConstraintViolationException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.*;

class CreateQuestionnaireUseCaseParamTest {

    private Long kitId;
    private Integer index;
    private String title;
    private String description;
    private Integer weight;
    private UUID currentUserId;

    @BeforeEach
    void setUp() {
        kitId = 1L;
        index = 2;
        title = "Clean Architecture";
        description = "Clean Architecture Description";
        currentUserId = UUID.randomUUID();
    }

    @Test
    void testCreateQuestionnaireUseCase_kitIdIsNull_ErrorMessage() {
        kitId = null;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateQuestionnaireUseCase.Param(kitId, index, title, description, currentUserId));
        assertThat(throwable).hasMessage("kitId: " + CREATE_QUESTIONNAIRE_KIT_ID_NOT_NULL);
    }

    @Test
    void testCreateQuestionnaireUseCase_indexIsNull_ErrorMessage() {
        index = null;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateQuestionnaireUseCase.Param(kitId, index, title, description, currentUserId));
        assertThat(throwable).hasMessage("index: " + CREATE_QUESTIONNAIRE_INDEX_NOT_NULL);
    }

    @Test
    void testCreateQuestionnaireUseCaseParam_titleParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateQuestionnaireUseCase.Param(kitId, index, null, description, currentUserId));
        assertThat(throwable).hasMessage("title: " + CREATE_QUESTIONNAIRE_TITLE_NOT_NULL);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateQuestionnaireUseCase.Param(kitId, index, "ab", description, currentUserId));
        assertThat(throwable).hasMessage("title: " + CREATE_QUESTIONNAIRE_TITLE_SIZE_MIN);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateQuestionnaireUseCase.Param(kitId, index, RandomStringUtils.randomAlphabetic(101), description, currentUserId));
        assertThat(throwable).hasMessage("title: " + CREATE_QUESTIONNAIRE_TITLE_SIZE_MAX);
    }

    @Test
    void testCreateQuestionnaireUseCaseParam_descriptionParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateQuestionnaireUseCase.Param(kitId, index, title, null, currentUserId));
        assertThat(throwable).hasMessage("description: " + CREATE_QUESTIONNAIRE_DESCRIPTION_NOT_NULL);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateQuestionnaireUseCase.Param(kitId, index, title, "ab", currentUserId));
        assertThat(throwable).hasMessage("description: " + CREATE_QUESTIONNAIRE_DESCRIPTION_SIZE_MIN);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateQuestionnaireUseCase.Param(kitId, index, title, RandomStringUtils.randomAlphabetic(501), currentUserId));
        assertThat(throwable).hasMessage("description: " + CREATE_QUESTIONNAIRE_DESCRIPTION_SIZE_MAX);
    }

    @Test
    void testCreateQuestionnaireUseCase_currentUserIdIsNull_ErrorMessage() {
        currentUserId = null;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateQuestionnaireUseCase.Param(kitId, index, title, description, currentUserId));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

}
