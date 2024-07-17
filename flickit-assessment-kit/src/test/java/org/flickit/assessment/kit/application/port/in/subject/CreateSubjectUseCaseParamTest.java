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

    private Long kitVersionId;
    private Integer index;
    private String title;
    private String description;
    private Integer weight;
    private Long expertGroupId;
    private UUID currentUserId;

    @BeforeEach
    void setUp() {
        kitVersionId = 1L;
        index = 2;
        title = "team";
        description = "about team";
        weight = 3;
        expertGroupId = 4L;
        currentUserId = UUID.randomUUID();
    }

    @Test
    void testCreateSubjectUseCase_kitVersionIdIsNull_ErrorMessage() {
        kitVersionId = null;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateSubjectUseCase.Param(kitVersionId, index, title, description, weight, expertGroupId, currentUserId));
        assertThat(throwable).hasMessage("kitVersionId: " + CREATE_SUBJECT_KIT_VERSION_ID_NOT_NULL);
    }

    @Test
    void testCreateSubjectUseCase_IndexIsNull_ErrorMessage() {
        index = null;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateSubjectUseCase.Param(kitVersionId, index, title, description, weight, expertGroupId, currentUserId));
        assertThat(throwable).hasMessage("index: " + CREATE_SUBJECT_INDEX_NOT_NULL);
    }

    @Test
    void testCreateSubjectUseCase_titleIsNull_ErrorMessage() {
        title = null;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateSubjectUseCase.Param(kitVersionId, index, title, description, weight, expertGroupId, currentUserId));
        assertThat(throwable).hasMessage("title: " + CREATE_SUBJECT_TITLE_NOT_BLANK);
    }

    @Test
    void testCreateSubjectUseCase_titleIsBlank_ErrorMessage() {
        title = " ";
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateSubjectUseCase.Param(kitVersionId, index, title, description, weight, expertGroupId, currentUserId));
        assertThat(throwable).hasMessage("title: " + CREATE_SUBJECT_TITLE_NOT_BLANK);
    }

    @Test
    void testCreateSubjectUseCase_titleIsEmpty_ErrorMessage() {
        title = "";
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateSubjectUseCase.Param(kitVersionId, index, title, description, weight, expertGroupId, currentUserId));
        assertThat(throwable).hasMessage("title: " + CREATE_SUBJECT_TITLE_NOT_BLANK);
    }

    @Test
    void testCreateSubjectUseCase_descriptionIsNull_ErrorMessage() {
        description = null;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateSubjectUseCase.Param(kitVersionId, index, title, description, weight, expertGroupId, currentUserId));
        assertThat(throwable).hasMessage("description: " + CREATE_SUBJECT_DESCRIPTION_NOT_BLANK);
    }

    @Test
    void testCreateSubjectUseCase_descriptionIsBlank_ErrorMessage() {
        description = " ";
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateSubjectUseCase.Param(kitVersionId, index, title, description, weight, expertGroupId, currentUserId));
        assertThat(throwable).hasMessage("description: " + CREATE_SUBJECT_DESCRIPTION_NOT_BLANK);
    }

    @Test
    void testCreateSubjectUseCase_descriptionIsEmpty_ErrorMessage() {
        description = "";
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateSubjectUseCase.Param(kitVersionId, index, title, description, weight, expertGroupId, currentUserId));
        assertThat(throwable).hasMessage("description: " + CREATE_SUBJECT_DESCRIPTION_NOT_BLANK);
    }

    @Test
    void testCreateSubjectUseCase_weightIsNull_ErrorMessage() {
        weight = null;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateSubjectUseCase.Param(kitVersionId, index, title, description, weight, expertGroupId, currentUserId));
        assertThat(throwable).hasMessage("weight: " + CREATE_SUBJECT_WEIGHT_NOT_NULL);
    }

    @Test
    void testCreateSubjectUseCase_expertGroupIdIsNull_ErrorMessage() {
        expertGroupId = null;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateSubjectUseCase.Param(kitVersionId, index, title, description, weight, expertGroupId, currentUserId));
        assertThat(throwable).hasMessage("expertGroupId: " + CREATE_SUBJECT_EXPERT_GROUP_ID_NOT_NULL);
    }

    @Test
    void testCreateSubjectUseCase_currentUserIdIsNull_ErrorMessage() {
        currentUserId = null;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateSubjectUseCase.Param(kitVersionId, index, title, description, weight, expertGroupId, currentUserId));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }
}
