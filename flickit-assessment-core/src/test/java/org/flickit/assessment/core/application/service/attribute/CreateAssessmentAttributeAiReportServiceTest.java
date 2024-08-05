package org.flickit.assessment.core.application.service.attribute;

import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.config.OpenAiProperties;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.CalculateNotValidException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.port.in.attribute.CreateAssessmentAttributeAiReportUseCase.Param;
import org.flickit.assessment.core.application.port.out.assessment.GetAssessmentPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.attribute.CreateAssessmentAttributeAiPort;
import org.flickit.assessment.core.application.port.out.attribute.LoadAttributePort;
import org.flickit.assessment.core.application.port.out.attributeinsight.CreateAttributeInsightPort;
import org.flickit.assessment.core.application.port.out.attributeinsight.LoadAttributeInsightPort;
import org.flickit.assessment.core.application.port.out.attributeinsight.UpdateAttributeInsightPort;
import org.flickit.assessment.core.test.fixture.application.AssessmentMother;
import org.flickit.assessment.core.test.fixture.application.AssessmentResultMother;
import org.flickit.assessment.core.test.fixture.application.AttributeInsightMother;
import org.flickit.assessment.core.test.fixture.application.AttributeMother;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Optional;
import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.EXPORT_ASSESSMENT_REPORT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_ASSESSMENT_RESULT_NOT_VALID;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.ASSESSMENT_ID_NOT_FOUND;
import static org.flickit.assessment.core.common.ErrorMessageKey.CREATE_ASSESSMENT_ATTRIBUTE_AI_REPORT_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.core.common.MessageKey.ASSESSMENT_ATTRIBUTE_AI_IS_DISABLED;
import static org.flickit.assessment.core.test.fixture.application.AttributeInsightMother.simpleAttributeAiInsightMinInsightTime;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateAssessmentAttributeAiReportServiceTest {

    private CreateAssessmentAttributeAiReportService service;

    @Mock
    private OpenAiProperties openAiProperties;

    @Mock
    private ValidateAssessmentResultPort validateAssessmentResultPort;

    @Mock
    private GetAssessmentPort getAssessmentPort;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private LoadAssessmentResultPort loadAssessmentResultPort;

    @Mock
    private CreateAssessmentAttributeAiPort createAssessmentAttributeAiPort;

    @Mock
    private UpdateAttributeInsightPort updateAttributeInsightPort;

    @Mock
    private LoadAttributePort loadAttributePort;

    @Mock
    private LoadAttributeInsightPort loadAttributeInsightPort;

    @Mock
    private CreateAttributeInsightPort createAttributeInsightPort;

    private final String fileLink = "http://127.0.0.1:9000/report/5e3b5d74-cc9c-4b54-b051-86e934ae9a03/temp.?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-" +
        "Credential=minioadmin%2F20240726%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20240726T052101Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-" +
        "Signature=8dfab4d27ab012f1ef15beb58b54da353049f00b9e4a53115eb385b41fb4f4a5";

    @BeforeEach
    void prepare() {
        service = spy(new CreateAssessmentAttributeAiReportService(getAssessmentPort, assessmentAccessChecker, loadAssessmentResultPort,
            validateAssessmentResultPort, loadAttributePort, openAiProperties, loadAttributeInsightPort, createAttributeInsightPort,
            updateAttributeInsightPort, createAssessmentAttributeAiPort));
    }

    @Test
    void testCreateAssessmentAttributeAiReport_AssessmentNotFound_ThrowResourceNotFoundException() {
        UUID assessmentId = UUID.randomUUID();
        Param param = new Param(assessmentId, 123L, fileLink, UUID.randomUUID());

        when(getAssessmentPort.getAssessmentById(param.getAssessmentId())).thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.createAttributeAiReport(param));
        assertEquals(ASSESSMENT_ID_NOT_FOUND, throwable.getMessage());

        verifyNoInteractions(assessmentAccessChecker,
            loadAttributePort,
            loadAssessmentResultPort,
            loadAttributeInsightPort,
            createAssessmentAttributeAiPort,
            updateAttributeInsightPort);
    }

    @Test
    void testCreateAssessmentAttributeAiReport_UserDoesNotHaveRequiredPermission_ThrowAccessDeniedException() {
        UUID currentUserId = UUID.randomUUID();
        var assessment = AssessmentMother.assessment();
        Param param = new Param(assessment.getId(), 123L, fileLink, currentUserId);

        when(getAssessmentPort.getAssessmentById(param.getAssessmentId())).thenReturn(Optional.of(assessment));
        when(assessmentAccessChecker.isAuthorized(assessment.getId(), param.getCurrentUserId(), EXPORT_ASSESSMENT_REPORT)).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.createAttributeAiReport(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(loadAttributePort,
            loadAssessmentResultPort,
            loadAttributeInsightPort,
            createAssessmentAttributeAiPort,
            updateAttributeInsightPort);
    }

    @Test
    void testCreateAssessmentAttributeAiReport_AssessmentResultNotFound_ThrowResourceNotFoundException() {
        UUID currentUserId = UUID.randomUUID();
        var assessment = AssessmentMother.assessment();
        Param param = new Param(assessment.getId(), 123L, fileLink, currentUserId);

        when(getAssessmentPort.getAssessmentById(param.getAssessmentId())).thenReturn(Optional.of(assessment));
        when(assessmentAccessChecker.isAuthorized(assessment.getId(), param.getCurrentUserId(), EXPORT_ASSESSMENT_REPORT)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(assessment.getId()))
            .thenThrow(new ResourceNotFoundException(CREATE_ASSESSMENT_ATTRIBUTE_AI_REPORT_ASSESSMENT_RESULT_NOT_FOUND));

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.createAttributeAiReport(param));
        assertEquals(CREATE_ASSESSMENT_ATTRIBUTE_AI_REPORT_ASSESSMENT_RESULT_NOT_FOUND, throwable.getMessage());

        verifyNoInteractions(loadAttributeInsightPort,
            createAssessmentAttributeAiPort,
            updateAttributeInsightPort);
    }

    @Test
    void testCreateAssessmentAttributeAiReport_CalculatedResultIsNotValid_ThrowCalculateNotValidException() {
        Long attributeId = 1L;
        UUID currentUserId = UUID.randomUUID();
        var assessmentResult = AssessmentResultMother.invalidResultWithSubjectValues(null);
        var assessment = assessmentResult.getAssessment();

        Param param = new Param(assessment.getId(), attributeId, fileLink, currentUserId);

        when(getAssessmentPort.getAssessmentById(param.getAssessmentId())).thenReturn(Optional.of(assessment));
        when(assessmentAccessChecker.isAuthorized(assessment.getId(), param.getCurrentUserId(), EXPORT_ASSESSMENT_REPORT)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(assessment.getId())).thenReturn(Optional.of(assessmentResult));
        doThrow(new CalculateNotValidException(COMMON_ASSESSMENT_RESULT_NOT_VALID)).when(validateAssessmentResultPort).validate(assessment.getId());

        var throwable = assertThrows(CalculateNotValidException.class, () -> service.createAttributeAiReport(param));
        assertEquals(COMMON_ASSESSMENT_RESULT_NOT_VALID, throwable.getMessage());

        verifyNoInteractions(loadAttributeInsightPort,
            createAssessmentAttributeAiPort,
            updateAttributeInsightPort);
    }

    @Test
    void testCreateAssessmentAttributeAiReport_AiInsightDoesNotExistAndAiEnabled_GenerateAndPersistAiInsight() {
        UUID currentUserId = UUID.randomUUID();
        var attribute = AttributeMother.simpleAttribute();
        var assessmentResult = AssessmentResultMother.validResultWithJustAnId();
        var assessment = assessmentResult.getAssessment();
        Param param = new Param(assessment.getId(), attribute.getId(), fileLink, currentUserId);
        InputStream downloadFileResult = new ByteArrayInputStream("File Content".getBytes());
        var aiReport = "Report Content";

        when(openAiProperties.isEnabled()).thenReturn(true);
        when(getAssessmentPort.getAssessmentById(param.getAssessmentId())).thenReturn(Optional.of(assessment));
        when(assessmentAccessChecker.isAuthorized(assessment.getId(), param.getCurrentUserId(), EXPORT_ASSESSMENT_REPORT)).thenReturn(true);
        doNothing().when(validateAssessmentResultPort).validate(assessment.getId());
        when(loadAssessmentResultPort.loadByAssessmentId(assessment.getId())).thenReturn(Optional.of(assessmentResult));
        when(loadAttributePort.load(attribute.getId(), assessmentResult.getKitVersionId())).thenReturn(attribute);
        when(loadAttributeInsightPort.loadAttributeAiInsight(assessmentResult.getId(), param.getAttributeId())).thenReturn(Optional.empty());
        when(createAssessmentAttributeAiPort.createReport(downloadFileResult, attribute)).thenReturn(aiReport);
        doNothing().when(createAttributeInsightPort).persist(any());
        doReturn(downloadFileResult).when(service).readInputFile(param.getFileLink());

        var result = service.createAttributeAiReport(param);
        assertEquals("Report Content", result.content());

        verifyNoInteractions(updateAttributeInsightPort);
    }

    @Test
    void testCreateAssessmentAttributeAiReport_AiInsightDoesNotExistAndAiDisabled_ReturnConstantMessage() {
        UUID currentUserId = UUID.randomUUID();
        var attribute = AttributeMother.simpleAttribute();
        var assessmentResult = AssessmentResultMother.validResultWithJustAnId();
        var assessment = assessmentResult.getAssessment();
        Param param = new Param(assessment.getId(), attribute.getId(), fileLink, currentUserId);

        when(openAiProperties.isEnabled()).thenReturn(false);
        when(getAssessmentPort.getAssessmentById(param.getAssessmentId())).thenReturn(Optional.of(assessment));
        when(assessmentAccessChecker.isAuthorized(assessment.getId(), param.getCurrentUserId(), EXPORT_ASSESSMENT_REPORT)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(assessment.getId())).thenReturn(Optional.of(assessmentResult));
        when(loadAttributePort.load(attribute.getId(), assessmentResult.getKitVersionId())).thenReturn(attribute);
        when(loadAttributeInsightPort.loadAttributeAiInsight(assessmentResult.getId(), param.getAttributeId())).thenReturn(Optional.empty());

        var result = service.createAttributeAiReport(param);
        assertEquals(MessageBundle.message(ASSESSMENT_ATTRIBUTE_AI_IS_DISABLED, attribute.getTitle()), result.content());

        verifyNoInteractions(updateAttributeInsightPort, createAssessmentAttributeAiPort, createAttributeInsightPort);
    }

    @Test
    void testCreateAssessmentAttributeAiReport_AiInsightExistsAndInsightTimeIsAfterCalculationTime_ReturnExistedInsight() {
        UUID currentUserId = UUID.randomUUID();
        UUID assessmentId = UUID.randomUUID();
        var attribute = AttributeMother.simpleAttribute();
        var assessmentResult = AssessmentResultMother.validResultWithJustAnId();
        var assessment = assessmentResult.getAssessment();
        Param param = new Param(assessmentId, attribute.getId(), fileLink, currentUserId);
        var attributeInsight = AttributeInsightMother.simpleAttributeAiInsight();

        when(getAssessmentPort.getAssessmentById(param.getAssessmentId())).thenReturn(Optional.of(assessment));
        when(assessmentAccessChecker.isAuthorized(assessment.getId(), param.getCurrentUserId(), EXPORT_ASSESSMENT_REPORT)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(assessment.getId())).thenReturn(Optional.of(assessmentResult));
        when(loadAttributePort.load(attribute.getId(), assessmentResult.getKitVersionId())).thenReturn(attribute);
        when(loadAttributeInsightPort.loadAttributeAiInsight(assessmentResult.getId(), param.getAttributeId()))
            .thenReturn(Optional.of(attributeInsight));

        var result = service.createAttributeAiReport(param);
        assertEquals(result.content(), attributeInsight.getAiInsight());

        verifyNoInteractions(createAssessmentAttributeAiPort, updateAttributeInsightPort);
    }

    @Test
    void testCreateAssessmentAttributeAiReport_AiInsightExistsAndInsightTimeIsBeforeCalculationTime_AiEnabled_RegenerateAndUpdateInsight() {
        UUID currentUserId = UUID.randomUUID();
        UUID assessmentId = UUID.randomUUID();
        var attribute = AttributeMother.simpleAttribute();
        var assessmentResult = AssessmentResultMother.validResultWithJustAnId();
        var assessment = assessmentResult.getAssessment();
        Param param = new Param(assessmentId, attribute.getId(), fileLink, currentUserId);
        var attributeInsight = simpleAttributeAiInsightMinInsightTime();
        InputStream downloadFileResult = new ByteArrayInputStream("File Content".getBytes());

        when(openAiProperties.isEnabled()).thenReturn(true);
        when(getAssessmentPort.getAssessmentById(param.getAssessmentId())).thenReturn(Optional.of(assessment));
        when(assessmentAccessChecker.isAuthorized(assessment.getId(), param.getCurrentUserId(), EXPORT_ASSESSMENT_REPORT)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(assessment.getId())).thenReturn(Optional.of(assessmentResult));
        when(loadAttributePort.load(attribute.getId(), assessmentResult.getKitVersionId())).thenReturn(attribute);
        when(loadAttributeInsightPort.loadAttributeAiInsight(assessmentResult.getId(), param.getAttributeId())).thenReturn(Optional.of(attributeInsight));
        doReturn(downloadFileResult).when(service).readInputFile(param.getFileLink());
        when(createAssessmentAttributeAiPort.createReport(downloadFileResult, attribute)).thenReturn(attributeInsight.getAiInsight());
        doNothing().when(updateAttributeInsightPort).updateAiInsight(any());

        var result = service.createAttributeAiReport(param);
        assertEquals(attributeInsight.getAiInsight(), result.content());

        verifyNoInteractions(createAttributeInsightPort);
    }

    @Test
    void testCreateAssessmentAttributeAiReport_AiInsightExistsAndInsightTimeIsBeforeCalculationTime_AiDisabled_ReturnConstantMessage() {
        UUID currentUserId = UUID.randomUUID();
        UUID assessmentId = UUID.randomUUID();
        var attribute = AttributeMother.simpleAttribute();
        var assessmentResult = AssessmentResultMother.validResultWithJustAnId();
        var assessment = assessmentResult.getAssessment();
        Param param = new Param(assessmentId, attribute.getId(), fileLink, currentUserId);
        var attributeInsight = simpleAttributeAiInsightMinInsightTime();

        when(openAiProperties.isEnabled()).thenReturn(false);
        when(getAssessmentPort.getAssessmentById(param.getAssessmentId())).thenReturn(Optional.of(assessment));
        when(assessmentAccessChecker.isAuthorized(assessment.getId(), param.getCurrentUserId(), EXPORT_ASSESSMENT_REPORT)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(assessment.getId())).thenReturn(Optional.of(assessmentResult));
        when(loadAttributePort.load(attribute.getId(), assessmentResult.getKitVersionId())).thenReturn(attribute);
        when(loadAttributeInsightPort.loadAttributeAiInsight(assessmentResult.getId(), param.getAttributeId())).thenReturn(Optional.of(attributeInsight));

        var result = service.createAttributeAiReport(param);
        assertEquals(MessageBundle.message(ASSESSMENT_ATTRIBUTE_AI_IS_DISABLED, attribute.getTitle()), result.content());

        verifyNoInteractions(createAssessmentAttributeAiPort, createAttributeInsightPort, updateAttributeInsightPort);
    }
}
