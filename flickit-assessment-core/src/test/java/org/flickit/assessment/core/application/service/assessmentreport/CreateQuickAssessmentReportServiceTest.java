package org.flickit.assessment.core.application.service.assessmentreport;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AssessmentReport;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.port.in.assessmentreport.PrepareReportUseCase;
import org.flickit.assessment.core.application.port.out.assessmentreport.CreateAssessmentReportPort;
import org.flickit.assessment.core.application.port.out.assessmentreport.LoadAssessmentReportPort;
import org.flickit.assessment.core.application.port.out.assessmentreport.UpdateAssessmentReportPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.service.insight.InitAssessmentInsightsHelper;
import org.flickit.assessment.core.application.service.insight.RegenerateExpiredInsightsHelper;
import org.flickit.assessment.core.test.fixture.application.AssessmentReportMother;
import org.flickit.assessment.core.test.fixture.application.AssessmentResultMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.CREATE_QUICK_ASSESSMENT_REPORT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrepareReportServiceTest {

    @InjectMocks
    private PrepareReportService service;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private LoadAssessmentResultPort loadAssessmentResultPort;

    @Mock
    private CreateAssessmentReportPort createAssessmentReportPort;

    @Mock
    private InitAssessmentInsightsHelper initAssessmentInsightsHelper;

    @Mock
    private RegenerateExpiredInsightsHelper regenerateExpiredInsightsHelper;

    @Mock
    private LoadAssessmentReportPort loadAssessmentReportPort;

    @Mock
    private UpdateAssessmentReportPort updateAssessmentReportPort;

    @Captor
    private ArgumentCaptor<CreateAssessmentReportPort.Param> argumentCaptor;

    @Captor
    private ArgumentCaptor<UpdateAssessmentReportPort.UpdatePublishParam> updatePublishPortParam;

    private final PrepareReportUseCase.Param param = createParam(PrepareReportUseCase.Param.ParamBuilder::build);
    private final AssessmentResult assessmentResult = AssessmentResultMother.validResult();

    @Test
    void testPrepareReport_whenCurrentUserDoesNotHaveRequiredPermission_thenThrowAccessDeniedException() {
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_QUICK_ASSESSMENT_REPORT))
                .thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.prepareReport(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(loadAssessmentResultPort, createAssessmentReportPort,
                initAssessmentInsightsHelper, regenerateExpiredInsightsHelper, loadAssessmentReportPort, updateAssessmentReportPort);
    }

    @Test
    void testPrepareReport_whenAssessmentResultNotExists_thenResourceNotFoundException() {
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_QUICK_ASSESSMENT_REPORT))
                .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.prepareReport(param));
        assertEquals(COMMON_ASSESSMENT_RESULT_NOT_FOUND, throwable.getMessage());

        verifyNoInteractions(createAssessmentReportPort, initAssessmentInsightsHelper, regenerateExpiredInsightsHelper,
                loadAssessmentReportPort, updateAssessmentReportPort);
    }

    @Test
    void testPrepareReport_whenParamsAreValid_thenInitOrGenerateInsightsWithCreatingReport() {
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_QUICK_ASSESSMENT_REPORT))
                .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadAssessmentReportPort.load(param.getAssessmentId())).thenReturn(Optional.empty());

        service.prepareReport(param);
        verify(createAssessmentReportPort).persist(argumentCaptor.capture());

        assertEquals(assessmentResult.getId(), argumentCaptor.getValue().assessmentResultId());
        assertEquals(param.getCurrentUserId(), argumentCaptor.getValue().createdBy());
        assertNotNull(argumentCaptor.getValue().creationTime());

        var locale = Locale.of(assessmentResult.getLanguage().getCode());
        verify(initAssessmentInsightsHelper).initInsights(assessmentResult, locale);
        verify(regenerateExpiredInsightsHelper).regenerateExpiredInsights(assessmentResult, locale);

        verifyNoInteractions(updateAssessmentReportPort);
    }

    @Test
    void testPrepareReport_whenParamsAreValidAndReportAlreadyExists_thenInitOrGenerateInsightsWithoutCreatingReport() {
        AssessmentReport assessmentReport = AssessmentReportMother.empty();

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_QUICK_ASSESSMENT_REPORT))
                .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadAssessmentReportPort.load(param.getAssessmentId())).thenReturn(Optional.of(assessmentReport));

        service.prepareReport(param);

        var locale = Locale.of(assessmentResult.getLanguage().getCode());
        verify(initAssessmentInsightsHelper).initInsights(assessmentResult, locale);
        verify(regenerateExpiredInsightsHelper).regenerateExpiredInsights(assessmentResult, locale);
        verify(updateAssessmentReportPort).updatePublishStatus(updatePublishPortParam.capture());

        assertEquals(assessmentResult.getId(), updatePublishPortParam.getValue().assessmentResultId());
        assertTrue(updatePublishPortParam.getValue().published());
        assertNotNull(updatePublishPortParam.getValue().lastModificationTime());
        assertEquals(assessmentReport.getVisibility(), updatePublishPortParam.getValue().visibilityType());
        assertEquals(param.getCurrentUserId(), updatePublishPortParam.getValue().lastModifiedBy());

        verifyNoInteractions(createAssessmentReportPort);
    }

    private PrepareReportUseCase.Param createParam(Consumer<PrepareReportUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private PrepareReportUseCase.Param.ParamBuilder paramBuilder() {
        return PrepareReportUseCase.Param.builder()
                .assessmentId(UUID.randomUUID())
                .currentUserId(UUID.randomUUID());
    }
}
