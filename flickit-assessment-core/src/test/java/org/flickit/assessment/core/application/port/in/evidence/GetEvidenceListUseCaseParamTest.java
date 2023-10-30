package org.flickit.assessment.core.application.port.in.evidence;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_EVIDENCE_LIST_ASSESSMENT_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_EVIDENCE_LIST_QUESTION_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class GetEvidenceListUseCaseParamTest {

    @Test
    void testGetEvidenceListParam_NullQuestion_ReturnErrorMessage() {
        UUID ASSESSMENT_ID = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetEvidenceListUseCase.Param(null, ASSESSMENT_ID, 10, 0));
        assertThat(throwable).hasMessage("questionId: " + GET_EVIDENCE_LIST_QUESTION_ID_NOT_NULL);
    }

    @Test
    void testGetEvidenceListParam_NullAssessment_ReturnErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetEvidenceListUseCase.Param(0L, null, 10, 0));
        assertThat(throwable).hasMessage("assessmentId: " + GET_EVIDENCE_LIST_ASSESSMENT_ID_NOT_NULL);
    }
}
