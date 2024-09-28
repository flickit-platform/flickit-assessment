package org.flickit.assessment.kit.application.port.in.maturitylevel;

import jakarta.validation.ConstraintViolationException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

    @Test
    void testCreateMaturityLevelUseCaseParam_titleParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateMaturityLevelUseCase.Param(kitId, index, null, description, value, currentUserId));
        assertThat(throwable).hasMessage("title: " + CREATE_MATURITY_LEVEL_TITLE_NOT_NULL);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateMaturityLevelUseCase.Param(kitId, index, "ab", description, value, currentUserId));
        assertThat(throwable).hasMessage("title: " + CREATE_MATURITY_LEVEL_TITLE_SIZE_MIN);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateMaturityLevelUseCase.Param(kitId, index, RandomStringUtils.randomAlphabetic(101), description, value, currentUserId));
        assertThat(throwable).hasMessage("title: " + CREATE_MATURITY_LEVEL_TITLE_SIZE_MAX);
    }

    @Test
    void testCreateMaturityLevelUseCaseParam_descriptionParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateMaturityLevelUseCase.Param(kitId, index, title, null, value, currentUserId));
        assertThat(throwable).hasMessage("description: " + CREATE_MATURITY_LEVEL_DESCRIPTION_NOT_NULL);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateMaturityLevelUseCase.Param(kitId, index, title, "ab", value, currentUserId));
        assertThat(throwable).hasMessage("description: " + CREATE_MATURITY_LEVEL_DESCRIPTION_SIZE_MIN);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateMaturityLevelUseCase.Param(kitId, index, title, RandomStringUtils.randomAlphabetic(501), value, currentUserId));
        assertThat(throwable).hasMessage("description: " + CREATE_MATURITY_LEVEL_DESCRIPTION_SIZE_MAX);
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
