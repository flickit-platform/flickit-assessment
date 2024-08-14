package org.flickit.assessment.kit.application.port.in.maturitylevel;

import jakarta.validation.ConstraintViolationException;
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

class CreateMaturityLevelUseCaseParamTest {

    private Long kitId;
    private Integer index;
    private String title;
    private String description;
    private Integer value;
    private UUID currentUserId;

    @BeforeEach
    void setUp() {
        kitId = 1L;
        index = 1;
        title = "basic";
        description = "basic level indicating fundamental and essential functionalities of the system.";
        value = 1;
        currentUserId = UUID.randomUUID();
    }

    @Test
    void testCreateMaturityLevelUseCase_kitIdIsNull_ErrorMessage() {
        kitId = null;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateMaturityLevelUseCase.Param(kitId, index, title, description, value, currentUserId));
        assertThat(throwable).hasMessage("kitId: " + CREATE_MATURITY_LEVEL_KIT_ID_NOT_NULL);
    }

    @Test
    void testCreateMaturityLevelUseCase_indexIsNull_ErrorMessage() {
        index = null;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateMaturityLevelUseCase.Param(kitId, index, title, description, value, currentUserId));
        assertThat(throwable).hasMessage("index: " + CREATE_MATURITY_LEVEL_INDEX_NOT_NULL);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    void testCreateMaturityLevelUseCase_titleIsBlank_ErrorMessage(String title) {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateMaturityLevelUseCase.Param(kitId, index, title, description, value, currentUserId));
        assertThat(throwable).hasMessage("title: " + CREATE_MATURITY_LEVEL_TITLE_NOT_BLANK);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    void testCreateMaturityLevelUseCase_descriptionIsBlank_ErrorMessage(String description) {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateMaturityLevelUseCase.Param(kitId, index, title, description, value, currentUserId));
        assertThat(throwable).hasMessage("description: " + CREATE_MATURITY_LEVEL_DESCRIPTION_NOT_BLANK);
    }

    @Test
    void testCreateMaturityLevelUseCase_valueIsNull_ErrorMessage() {
        value = null;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateMaturityLevelUseCase.Param(kitId, index, title, description, value, currentUserId));
        assertThat(throwable).hasMessage("value: " + CREATE_MATURITY_LEVEL_VALUE_NOT_NULL);
    }

    @Test
    void testCreateMaturityLevelUseCase_currentUserIdIsNull_ErrorMessage() {
        currentUserId = null;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateMaturityLevelUseCase.Param(kitId, index, title, description, value, currentUserId));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }
}
