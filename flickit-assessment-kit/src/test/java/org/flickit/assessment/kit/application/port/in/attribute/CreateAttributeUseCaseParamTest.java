package org.flickit.assessment.kit.application.port.in.attribute;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.*;

class CreateAttributeUseCaseParamTest {

    private Long kitId;
    private Integer index;
    private String title;
    private String description;
    private Integer weight;
    private Long subjectId;
    private UUID currentUserId;

    @BeforeEach
    void setUp() {
        kitId = 1L;
        index = 1;
        title = "software quality";
        description = "about software quality";
        weight = 1;
        subjectId = 1L;
        currentUserId = UUID.randomUUID();
    }

    @Test
    void testCreateAttributeUseCase_kitIdIsNull_ErrorMessage() {
        kitId = null;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateAttributeUseCase.Param(kitId, index, title, description, weight, subjectId, currentUserId));
        assertThat(throwable).hasMessage("kitId: " + CREATE_ATTRIBUTE_KIT_ID_NOT_NULL);
    }

    @Test
    void testCreateAttributeUseCase_indexIsNull_ErrorMessage() {
        index = null;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateAttributeUseCase.Param(kitId, index, title, description, weight, subjectId, currentUserId));
        assertThat(throwable).hasMessage("index: " + CREATE_ATTRIBUTE_INDEX_NOT_NULL);
    }

    @Test
    void testCreateAttributeUseCase_titleIsNull_ErrorMessage() {
        title = null;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateAttributeUseCase.Param(kitId, index, title, description, weight, subjectId, currentUserId));
        assertThat(throwable).hasMessage("title: " + CREATE_ATTRIBUTE_TITLE_NOT_BLANK);
    }

    @Test
    void testCreateAttributeUseCase_titleIsBlank_ErrorMessage() {
        title = " ";
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateAttributeUseCase.Param(kitId, index, title, description, weight, subjectId, currentUserId));
        assertThat(throwable).hasMessage("title: " + CREATE_ATTRIBUTE_TITLE_NOT_BLANK);
    }

    @Test
    void testCreateAttributeUseCase_titleIsEmpty_ErrorMessage() {
        title = "";
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateAttributeUseCase.Param(kitId, index, title, description, weight, subjectId, currentUserId));
        assertThat(throwable).hasMessage("title: " + CREATE_ATTRIBUTE_TITLE_NOT_BLANK);
    }

    @Test
    void testCreateAttributeUseCase_descriptionIsNull_ErrorMessage() {
        description = null;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateAttributeUseCase.Param(kitId, index, title, description, weight, subjectId, currentUserId));
        assertThat(throwable).hasMessage("description: " + CREATE_ATTRIBUTE_DESCRIPTION_NOT_BLANK);
    }

    @Test
    void testCreateAttributeUseCase_descriptionIsBlank_ErrorMessage() {
        description = " ";
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateAttributeUseCase.Param(kitId, index, title, description, weight, subjectId, currentUserId));
        assertThat(throwable).hasMessage("description: " + CREATE_ATTRIBUTE_DESCRIPTION_NOT_BLANK);
    }

    @Test
    void testCreateAttributeUseCase_descriptionIsEmpty_ErrorMessage() {
        description = "";
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateAttributeUseCase.Param(kitId, index, title, description, weight, subjectId, currentUserId));
        assertThat(throwable).hasMessage("description: " + CREATE_ATTRIBUTE_DESCRIPTION_NOT_BLANK);
    }

    @Test
    void testCreateAttributeUseCase_weightIsNull_ErrorMessage() {
        weight = null;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateAttributeUseCase.Param(kitId, index, title, description, weight, subjectId, currentUserId));
        assertThat(throwable).hasMessage("weight: " + CREATE_ATTRIBUTE_WEIGHT_NOT_NULL);
    }

    @Test
    void testCreateAttributeUseCase_subjectIdIsNull_ErrorMessage() {
        subjectId = null;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateAttributeUseCase.Param(kitId, index, title, description, weight, subjectId, currentUserId));
        assertThat(throwable).hasMessage("subjectId: " + CREATE_ATTRIBUTE_SUBJECT_ID_NOT_NULL);
    }

    @Test
    void testCreateAttributeUseCase_currentUserIdIsNull_ErrorMessage() {
        currentUserId = null;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateAttributeUseCase.Param(kitId, index, title, description, weight, subjectId, currentUserId));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }
}
