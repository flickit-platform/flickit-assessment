package org.flickit.assessment.kit.application.port.in.subject;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.*;

class UpdateSubjectUseCaseParamTest {

    private Long kitId;
    private Long subjectId;
    private Integer index;
    private String title;
    private String description;
    private Integer weight;
    private UUID currentUserId;

    @BeforeEach
    void setUp() {
        kitId = 1L;
        subjectId = 1L;
        index = 2;
        title = "team";
        description = "about team";
        weight = 3;
        currentUserId = UUID.randomUUID();
    }

    @Test
    void testUpdateSubjectByWizardUseCase_kitIdIsNull_ErrorMessage() {
        kitId = null;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateSubjectUseCase.Param(kitId,
                subjectId,
                index,
                title,
                description,
                weight,
                currentUserId));
        assertThat(throwable).hasMessage("kitId: " + UPDATE_SUBJECT_BY_WIZARD_KIT_ID_NOT_NULL);
    }

    @Test
    void testUpdateSubjectByWizardUseCase_subjectIdIsNull_ErrorMessage() {
        subjectId = null;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateSubjectUseCase.Param(kitId,
                subjectId,
                index,
                title,
                description,
                weight,
                currentUserId));
        assertThat(throwable).hasMessage("subjectId: " + UPDATE_SUBJECT_BY_WIZARD_SUBJECT_ID_NOT_NULL);
    }

    @Test
    void testUpdateSubjectByWizardUseCase_indexIsNull_ErrorMessage() {
        index = null;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateSubjectUseCase.Param(kitId,
                subjectId,
                index,
                title,
                description,
                weight,
                currentUserId));
        assertThat(throwable).hasMessage("index: " + UPDATE_SUBJECT_BY_WIZARD_INDEX_NOT_NULL);
    }

    @Test
    void testUpdateSubjectByWizardUseCase_titleIsNull_ErrorMessage() {
        title = null;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateSubjectUseCase.Param(kitId,
                subjectId,
                index,
                title,
                description,
                weight,
                currentUserId));
        assertThat(throwable).hasMessage("title: " + UPDATE_SUBJECT_BY_WIZARD_TITLE_NOT_BLANK);
    }

    @Test
    void testUpdateSubjectByWizardUseCase_titleIsBlank_ErrorMessage() {
        title = " ";
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateSubjectUseCase.Param(kitId,
                subjectId,
                index,
                title,
                description,
                weight,
                currentUserId));
        assertThat(throwable).hasMessage("title: " + UPDATE_SUBJECT_BY_WIZARD_TITLE_NOT_BLANK);
    }

    @Test
    void testUpdateSubjectByWizardUseCase_titleIsEmpty_ErrorMessage() {
        title = "";
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateSubjectUseCase.Param(kitId,
                subjectId,
                index,
                title,
                description,
                weight,
                currentUserId));
        assertThat(throwable).hasMessage("title: " + UPDATE_SUBJECT_BY_WIZARD_TITLE_NOT_BLANK);
    }

    @Test
    void testUpdateSubjectByWizardUseCase_descriptionIsNull_ErrorMessage() {
        description = null;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateSubjectUseCase.Param(kitId,
                subjectId,
                index,
                title,
                description,
                weight,
                currentUserId));
        assertThat(throwable).hasMessage("description: " + UPDATE_SUBJECT_BY_WIZARD_DESCRIPTION_NOT_BLANK);
    }

    @Test
    void testUpdateSubjectByWizardUseCase_descriptionIsBlank_ErrorMessage() {
        description = " ";
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateSubjectUseCase.Param(kitId,
                subjectId,
                index,
                title,
                description,
                weight,
                currentUserId));
        assertThat(throwable).hasMessage("description: " + UPDATE_SUBJECT_BY_WIZARD_DESCRIPTION_NOT_BLANK);
    }

    @Test
    void testUpdateSubjectByWizardUseCase_descriptionIsEmpty_ErrorMessage() {
        description = "";
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateSubjectUseCase.Param(kitId,
                subjectId,
                index,
                title,
                description,
                weight,
                currentUserId));
        assertThat(throwable).hasMessage("description: " + UPDATE_SUBJECT_BY_WIZARD_DESCRIPTION_NOT_BLANK);
    }

    @Test
    void testUpdateSubjectByWizardUseCase_weightIsNull_ErrorMessage() {
        weight = null;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateSubjectUseCase.Param(kitId,
                subjectId,
                index,
                title,
                description,
                weight,
                currentUserId));
        assertThat(throwable).hasMessage("weight: " + UPDATE_SUBJECT_BY_WIZARD_WEIGHT_NOT_NULL);
    }

    @Test
    void testUpdateSubjectByWizardUseCase_currentUserIdIsNull_ErrorMessage() {
        currentUserId = null;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateSubjectUseCase.Param(kitId,
                subjectId,
                index,
                title,
                description,
                weight,
                currentUserId));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }
}
