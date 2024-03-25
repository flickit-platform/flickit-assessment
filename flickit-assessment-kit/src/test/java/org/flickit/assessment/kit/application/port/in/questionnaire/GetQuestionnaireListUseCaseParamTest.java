package org.flickit.assessment.kit.application.port.in.questionnaire;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_QUESTIONNAIRE_LIST_ASSESSMENT_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GetQuestionnaireListUseCaseParamTest {

    @Test
    void testGetQuestionnaireListParam_assessmentIdIsBlank_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetQuestionnaireListUseCase.Param(null));
        assertThat(throwable).hasMessage("assessmentId: " + GET_QUESTIONNAIRE_LIST_ASSESSMENT_ID_NOT_NULL);
    }
}
