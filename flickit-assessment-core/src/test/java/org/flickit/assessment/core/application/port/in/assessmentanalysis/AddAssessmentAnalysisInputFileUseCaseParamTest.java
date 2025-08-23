package org.flickit.assessment.core.application.port.in.assessmentanalysis;

import jakarta.validation.ConstraintViolationException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.util.UUID;

import static io.jsonwebtoken.lang.Classes.getResourceAsStream;
import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AddAssessmentAnalysisInputFileUseCaseParamTest {

    @Test
    @SneakyThrows
    void testAddAssessmentAnalysisInputFileParam_AssessmentIdIsNull_ErrorMessage() {
        var inputFile = new MockMultipartFile("file", "file1",
                "application/vnd.rar", getResourceAsStream("/no-where/nothing.rar"));
        var currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
                () -> new AddAssessmentAnalysisInputFileUseCase.Param(null, inputFile, 1, currentUserId));
        assertThat(throwable).hasMessage("assessmentId: " + ADD_ASSESSMENT_ANALYSIS_INPUT_FILE_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    @SneakyThrows
    void testAddAssessmentAnalysisInputFileParam_InputFileIsNull_ErrorMessage() {
        var assessmentId = UUID.randomUUID();
        var currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
                () -> new AddAssessmentAnalysisInputFileUseCase.Param(assessmentId, null, 1, currentUserId));
        assertThat(throwable).hasMessage("inputFile: " + ADD_ASSESSMENT_ANALYSIS_INPUT_FILE_INPUT_FILE_NOT_NULL);
    }

    @Test
    @SneakyThrows
    void testAddAssessmentAnalysisInputFileParam_AnalysisTypeIsNull_ErrorMessage() {
        var assessmentId = UUID.randomUUID();
        var inputFile = new MockMultipartFile("file", "file1",
                "application/vnd.rar", getResourceAsStream("/no-where/nothing.rar"));
        var currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
                () -> new AddAssessmentAnalysisInputFileUseCase.Param(assessmentId, inputFile, null, currentUserId));
        assertThat(throwable).hasMessage("analysisType: " + ADD_ASSESSMENT_ANALYSIS_INPUT_FILE_ANALYSIS_TYPE_NOT_NULL);
    }

    @Test
    @SneakyThrows
    void testAddAssessmentAnalysisInputFileParam_CurrentUserIdIsNull_ErrorMessage() {
        var assessmentId = UUID.randomUUID();
        var inputFile = new MockMultipartFile("file", "file1",
                "application/vnd.rar", getResourceAsStream("/no-where/nothing.rar"));
        var throwable = assertThrows(ConstraintViolationException.class,
                () -> new AddAssessmentAnalysisInputFileUseCase.Param(assessmentId, inputFile, 1, null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }
}