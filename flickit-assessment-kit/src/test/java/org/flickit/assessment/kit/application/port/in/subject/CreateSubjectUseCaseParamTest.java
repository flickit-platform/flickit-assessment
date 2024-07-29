package org.flickit.assessment.kit.application.port.in.subject;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.*;

class CreateSubjectUseCaseParamTest {

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
        title = "team";
        description = "about team";
        weight = 3;
        currentUserId = UUID.randomUUID();
    }

    @Test
    void testCreateSubjectUseCase_kitIsNull_ErrorMessage() {
        kitId = null;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateSubjectUseCase.Param(kitId, index, title, description, weight, currentUserId));
        assertThat(throwable).hasMessage("kitId: " + CREATE_SUBJECT_KIT_ID_NOT_NULL);
    }

    @Test
    void testCreateSubjectUseCase_IndexIsNull_ErrorMessage() {
        index = null;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateSubjectUseCase.Param(kitId, index, title, description, weight, currentUserId));
        assertThat(throwable).hasMessage("index: " + CREATE_SUBJECT_INDEX_NOT_NULL);
    }

    @Test
    void testCreateSubjectUseCase_titleIsNull_ErrorMessage() {
        title = null;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateSubjectUseCase.Param(kitId, index, title, description, weight, currentUserId));
        assertThat(throwable).hasMessage("title: " + CREATE_SUBJECT_TITLE_NOT_BLANK);
    }

    @Test
    void testCreateSubjectUseCase_titleIsBlank_ErrorMessage() {
        title = " ";
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateSubjectUseCase.Param(kitId, index, title, description, weight, currentUserId));
        assertThat(throwable).hasMessage("title: " + CREATE_SUBJECT_TITLE_NOT_BLANK);
    }

    @Test
    void testCreateSubjectUseCase_titleIsEmpty_ErrorMessage() {
        title = "";
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateSubjectUseCase.Param(kitId, index, title, description, weight, currentUserId));
        assertThat(throwable).hasMessage("title: " + CREATE_SUBJECT_TITLE_NOT_BLANK);
    }

    @Test
    void testCreateSubjectUseCase_descriptionIsNull_ErrorMessage() {
        description = null;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateSubjectUseCase.Param(kitId, index, title, description, weight, currentUserId));
        assertThat(throwable).hasMessage("description: " + CREATE_SUBJECT_DESCRIPTION_NOT_BLANK);
    }

    @Test
    void testCreateSubjectUseCase_descriptionIsBlank_ErrorMessage() {
        description = " ";
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateSubjectUseCase.Param(kitId, index, title, description, weight, currentUserId));
        assertThat(throwable).hasMessage("description: " + CREATE_SUBJECT_DESCRIPTION_NOT_BLANK);
    }

    @Test
    void testCreateSubjectUseCase_descriptionIsEmpty_ErrorMessage() {
        description = "";
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateSubjectUseCase.Param(kitId, index, title, description, weight, currentUserId));
        assertThat(throwable).hasMessage("description: " + CREATE_SUBJECT_DESCRIPTION_NOT_BLANK);
    }

    @Test
    void testCreateSubjectUseCase_weightIsNull_ErrorMessage() {
        weight = null;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateSubjectUseCase.Param(kitId, index, title, description, weight, currentUserId));
        assertThat(throwable).hasMessage("weight: " + CREATE_SUBJECT_WEIGHT_NOT_NULL);
    }

    @Test
    void testCreateSubjectUseCase_currentUserIdIsNull_ErrorMessage() {
        currentUserId = null;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateSubjectUseCase.Param(kitId, index, title, description, weight, currentUserId));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }
}
