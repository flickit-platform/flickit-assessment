package org.flickit.assessment.core.application.port.in.attribute;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.CREATE_ATTRIBUTE_VALUE_EXCEL_ASSESSMENT_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.CREATE_ATTRIBUTE_VALUE_EXCEL_ATTRIBUTE_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class CreateAttributeValueExcelUseCaseParamTest {

    @Test
    void testCreateAttributeValueExcel_AssessmentIdIsNull_ErrorMessage() {
        UUID userId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateAttributeValueExcelUseCase.Param(null, 1L, userId));
        assertThat(throwable).hasMessage("assessmentId: " + CREATE_ATTRIBUTE_VALUE_EXCEL_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void testCreateAttributeValueExcel_AttributeIdIsNull_ErrorMessage() {
        UUID userId = UUID.randomUUID();
        UUID assessmentId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateAttributeValueExcelUseCase.Param(assessmentId, null, userId));
        assertThat(throwable).hasMessage("attributeId: " + CREATE_ATTRIBUTE_VALUE_EXCEL_ATTRIBUTE_ID_NOT_NULL);
    }

    @Test
    void testCreateAttributeValueExcel_CurrentUserIdIsNull_ErrorMessage() {
        UUID assessmentId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateAttributeValueExcelUseCase.Param(assessmentId, 1L, null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

}
