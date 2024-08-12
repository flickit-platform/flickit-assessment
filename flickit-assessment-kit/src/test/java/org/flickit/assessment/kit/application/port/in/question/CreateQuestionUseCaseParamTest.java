package org.flickit.assessment.kit.application.port.in.question;

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

class CreateQuestionUseCaseParamTest {

    private Long kitId;
    private Integer index;
    private String title;
    private String hint;
    private Boolean mayNotBeApplicable;
    private Boolean advisable;
    private Long questionnaireId;
    private UUID currentUserId;

    @BeforeEach
    void setUp() {
        kitId = 1L;
        index = 1;
        title = "question title";
        hint = "question hint";
        mayNotBeApplicable = false;
        advisable = true;
        questionnaireId = 1L;
        currentUserId = UUID.randomUUID();
    }

    @Test
    void testCreateQuestionUseCase_kitIdIsNull_ErrorMessage() {
        kitId = null;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateQuestionUseCase.Param(kitId,
                index,
                title,
                hint,
                mayNotBeApplicable,
                advisable,
                questionnaireId,
                currentUserId));
        assertThat(throwable).hasMessage("kitId: " + CREATE_QUESTION_KIT_ID_NOT_NULL);
    }

    @Test
    void testCreateQuestionUseCase_indexIsNull_ErrorMessage() {
        index = null;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateQuestionUseCase.Param(kitId,
                index,
                title,
                hint,
                mayNotBeApplicable,
                advisable,
                questionnaireId,
                currentUserId));
        assertThat(throwable).hasMessage("index: " + CREATE_QUESTION_INDEX_NOT_NULL);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    void testCreateQuestionUseCase_titleIsBlank_ErrorMessage(String title) {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateQuestionUseCase.Param(kitId,
                index,
                title,
                hint,
                mayNotBeApplicable,
                advisable,
                questionnaireId,
                currentUserId));
        assertThat(throwable).hasMessage("title: " + CREATE_QUESTION_TITLE_NOT_BLANK);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    void testCreateQuestionUseCase_hintIsBlank_ErrorMessage(String hint) {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateQuestionUseCase.Param(kitId,
                index,
                title,
                hint,
                mayNotBeApplicable,
                advisable,
                questionnaireId,
                currentUserId));
        assertThat(throwable).hasMessage("hint: " + CREATE_QUESTION_HINT_NOT_BLANK);
    }

    @Test
    void testCreateQuestionUseCase_mayNotBeApplicableIsNull_ErrorMessage() {
        mayNotBeApplicable = null;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateQuestionUseCase.Param(kitId,
                index,
                title,
                hint,
                mayNotBeApplicable,
                advisable,
                questionnaireId,
                currentUserId));
        assertThat(throwable).hasMessage("mayNotBeApplicable: " + CREATE_QUESTION_MAY_NOT_BE_APPLICABLE_NOT_NULL);
    }

    @Test
    void testCreateQuestionUseCase_advisableIsNull_ErrorMessage() {
        advisable = null;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateQuestionUseCase.Param(kitId,
                index,
                title,
                hint,
                mayNotBeApplicable,
                advisable,
                questionnaireId,
                currentUserId));
        assertThat(throwable).hasMessage("advisable: " + CREATE_QUESTION_ADVISABLE_NOT_NULL);
    }

    @Test
    void testCreateQuestionUseCase_questionnaireIdIsNull_ErrorMessage() {
        questionnaireId = null;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateQuestionUseCase.Param(kitId,
                index,
                title,
                hint,
                mayNotBeApplicable,
                advisable,
                questionnaireId,
                currentUserId));
        assertThat(throwable).hasMessage("questionnaireId: " + CREATE_QUESTION_QUESTIONNAIRE_ID_NOT_NULL);
    }

    @Test
    void testCreateQuestionUseCase_currentUserIdIsNull_ErrorMessage() {
        currentUserId = null;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateQuestionUseCase.Param(kitId,
                index,
                title,
                hint,
                mayNotBeApplicable,
                advisable,
                questionnaireId,
                currentUserId));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }
}
