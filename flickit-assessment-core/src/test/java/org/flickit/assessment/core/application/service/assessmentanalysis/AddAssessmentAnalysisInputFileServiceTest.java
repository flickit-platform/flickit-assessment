package org.flickit.assessment.core.application.service.assessmentanalysis;

import lombok.SneakyThrows;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AnalysisType;
import org.flickit.assessment.core.application.domain.AssessmentAnalysis;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.port.in.assessmentanalysis.AddAssessmentAnalysisInputFileUseCase;
import org.flickit.assessment.core.application.port.out.assessmentanalysis.CreateAssessmentAnalysisPort;
import org.flickit.assessment.core.application.port.out.assessmentanalysis.LoadAssessmentAnalysisPort;
import org.flickit.assessment.core.application.port.out.assessmentanalysis.UpdateAssessmentAnalysisInputPathPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.minio.CreateFileDownloadLinkPort;
import org.flickit.assessment.core.application.port.out.minio.DeleteFilePort;
import org.flickit.assessment.core.application.port.out.minio.UploadAssessmentAnalysisInputFilePort;
import org.flickit.assessment.core.test.fixture.application.AssessmentAnalysisMother;
import org.flickit.assessment.core.test.fixture.application.AssessmentResultMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Optional;
import java.util.UUID;

import static io.jsonwebtoken.lang.Classes.getResourceAsStream;
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.MANAGE_ADD_ON;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.ANALYSIS_TYPE_ID_NOT_VALID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddAssessmentAnalysisInputFileServiceTest {

    @InjectMocks
    private AddAssessmentAnalysisInputFileService service;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private LoadAssessmentResultPort loadAssessmentResultPort;

    @Mock
    private UploadAssessmentAnalysisInputFilePort uploadAssessmentAnalysisInputFilePort;

    @Mock
    private LoadAssessmentAnalysisPort loadAssessmentAnalysisPort;

    @Mock
    private CreateAssessmentAnalysisPort createAssessmentAnalysisPort;

    @Mock
    private DeleteFilePort deleteFilePort;

    @Mock
    private UpdateAssessmentAnalysisInputPathPort updateAssessmentAnalysisInputPathPort;

    @Mock
    private CreateFileDownloadLinkPort createFileDownloadLinkPort;

    @SneakyThrows
    @Test
    void testAddAssessmentAnalysisInputFile_UserDoesNotHaveRequiredPermission_ThrowAccessDeniedException() {
        var inputFile = new MockMultipartFile("file", "file1",
            "application/vnd.rar", getResourceAsStream("/no-where/nothing.rar"));
        var param = new AddAssessmentAnalysisInputFileUseCase.Param(
            UUID.randomUUID(),
            inputFile,
            1,
            UUID.randomUUID()
        );

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), MANAGE_ADD_ON)).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.addAssessmentAnalysisInputFile(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(loadAssessmentResultPort,
            uploadAssessmentAnalysisInputFilePort,
            loadAssessmentAnalysisPort,
            createAssessmentAnalysisPort,
            deleteFilePort,
            updateAssessmentAnalysisInputPathPort,
            createFileDownloadLinkPort);
    }

    @SneakyThrows
    @Test
    void testAddAssessmentAnalysisInputFile_AnalysisTypeIsNotValid_ThrowNotFoundException() {
        var inputFile = new MockMultipartFile("file", "file1",
            "application/vnd.rar", getResourceAsStream("/no-where/nothing.rar"));
        var param = new AddAssessmentAnalysisInputFileUseCase.Param(
            UUID.randomUUID(),
            inputFile,
            0,
            UUID.randomUUID()
        );

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), MANAGE_ADD_ON)).thenReturn(true);

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.addAssessmentAnalysisInputFile(param));
        assertEquals(ANALYSIS_TYPE_ID_NOT_VALID, throwable.getMessage());

        verifyNoInteractions(loadAssessmentResultPort,
            uploadAssessmentAnalysisInputFilePort,
            loadAssessmentAnalysisPort,
            createAssessmentAnalysisPort,
            deleteFilePort,
            updateAssessmentAnalysisInputPathPort,
            createFileDownloadLinkPort);
    }

    @SneakyThrows
    @Test
    void testAddAssessmentAnalysisInputFile_AssessmentAnalysisNotExist_PersistAssessmentAnalysis() {
        var inputFile = new MockMultipartFile("file", "file1",
            "application/vnd.rar", getResourceAsStream("/no-where/nothing.rar"));
        var param = new AddAssessmentAnalysisInputFileUseCase.Param(
            UUID.randomUUID(),
            inputFile,
            1,
            UUID.randomUUID()
        );
        AssessmentResult assessmentResult = AssessmentResultMother.validResultWithJustAnId();
        var inputPath = "path/to/input";
        String inputFileLink = "https://file/link";

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), MANAGE_ADD_ON)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadAssessmentAnalysisPort.load(assessmentResult.getId(), param.getAnalysisType())).thenReturn(Optional.empty());
        when(uploadAssessmentAnalysisInputFilePort.uploadAssessmentAnalysisInputFile(inputFile)).thenReturn(inputPath);

        when(createAssessmentAnalysisPort.persist(any())).thenReturn(UUID.randomUUID());
        when(createFileDownloadLinkPort.createDownloadLink(eq(inputPath), any())).thenReturn(inputFileLink);

        AddAssessmentAnalysisInputFileUseCase.Result result = service.addAssessmentAnalysisInputFile(param);
        var createParamArgument = ArgumentCaptor.forClass(CreateAssessmentAnalysisPort.Param.class);
        verify(createAssessmentAnalysisPort).persist(createParamArgument.capture());

        assertEquals(assessmentResult.getId(), createParamArgument.getValue().assessmentResultId());
        assertEquals(inputPath, createParamArgument.getValue().inputPath());
        assertEquals(AnalysisType.valueOfById(param.getAnalysisType()), createParamArgument.getValue().type());

        assertEquals(inputFileLink, result.fileLink());
        verifyNoInteractions(deleteFilePort,
            updateAssessmentAnalysisInputPathPort);
    }

    @SneakyThrows
    @Test
    void testAddAssessmentAnalysisInputFile_AssessmentAnalysisExists_UpdateAssessmentAnalysis() {
        var inputFile = new MockMultipartFile("file", "file1",
            "application/vnd.rar", getResourceAsStream("/no-where/nothing.rar"));
        var param = new AddAssessmentAnalysisInputFileUseCase.Param(
            UUID.randomUUID(),
            inputFile,
            1,
            UUID.randomUUID()
        );
        AssessmentResult assessmentResult = AssessmentResultMother.validResultWithJustAnId();
        AssessmentAnalysis assessmentAnalysis = AssessmentAnalysisMother.assessmentAnalysis();
        var inputPath = "path/to/input";
        String inputFileLink = "https://file/link";

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), MANAGE_ADD_ON)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(uploadAssessmentAnalysisInputFilePort.uploadAssessmentAnalysisInputFile(inputFile)).thenReturn(inputPath);

        when(loadAssessmentAnalysisPort.load(assessmentResult.getId(), param.getAnalysisType())).thenReturn(Optional.of(assessmentAnalysis));
        doNothing().when(updateAssessmentAnalysisInputPathPort).updateInputPath(assessmentAnalysis.getId(), inputPath);
        doNothing().when(deleteFilePort).deleteFile(assessmentAnalysis.getInputPath());
        when(createFileDownloadLinkPort.createDownloadLink(eq(inputPath), any())).thenReturn(inputFileLink);

        AddAssessmentAnalysisInputFileUseCase.Result result = service.addAssessmentAnalysisInputFile(param);

        assertEquals(inputFileLink, result.fileLink());
        verifyNoInteractions(createAssessmentAnalysisPort);
    }
}
