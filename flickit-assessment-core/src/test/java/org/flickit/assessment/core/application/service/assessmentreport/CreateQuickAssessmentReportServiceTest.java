package org.flickit.assessment.core.application.service.assessmentreport;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.port.in.assessmentreport.CreateQuickAssessmentReportUseCase;
import org.flickit.assessment.core.application.port.out.assessmentreport.CreateAssessmentReportPort;
import org.flickit.assessment.core.application.port.out.assessmentreport.LoadAssessmentReportPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.service.insight.InitInsightsHelper;
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
class CreateQuickAssessmentReportServiceTest {

    @InjectMocks
    private CreateQuickAssessmentReportService service;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private LoadAssessmentResultPort loadAssessmentResultPort;

    @Mock
    private CreateAssessmentReportPort createAssessmentReportPort;

    @Mock
    private InitInsightsHelper initInsightsHelper;

    @Mock
    private RegenerateExpiredInsightsHelper regenerateExpiredInsightsHelper;

    @Mock
    private LoadAssessmentReportPort loadAssessmentReportPort;

    @Captor
    private ArgumentCaptor<CreateAssessmentReportPort.QuickAssessmentReportParam> argumentCaptor;

    private final CreateQuickAssessmentReportUseCase.Param param = createParam(CreateQuickAssessmentReportUseCase.Param.ParamBuilder::build);
    private final AssessmentResult assessmentResult = AssessmentResultMother.validResult();

    @Test
    void testCreateQuickAssessmentReport_whenCurrentUserDoesNotHaveRequiredPermission_thenThrowAccessDeniedException() {
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_QUICK_ASSESSMENT_REPORT))
            .thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.create(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(loadAssessmentResultPort, createAssessmentReportPort, initInsightsHelper, regenerateExpiredInsightsHelper, loadAssessmentReportPort);
    }

    @Test
    void testCreateAssessmentReport_whenAssessmentResultNotExists_thenResourceNotFoundException() {
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_QUICK_ASSESSMENT_REPORT))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.create(param));
        assertEquals(COMMON_ASSESSMENT_RESULT_NOT_FOUND, throwable.getMessage());

        verifyNoInteractions(createAssessmentReportPort, initInsightsHelper, regenerateExpiredInsightsHelper, loadAssessmentReportPort);
    }

    @Test
    void testCreateAssessmentReport_whenParamsAreValid_thenInitOrGenerateInsightsWithCreatingReport() {
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_QUICK_ASSESSMENT_REPORT))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadAssessmentReportPort.load(param.getAssessmentId())).thenReturn(Optional.empty());

        service.create(param);
        verify(createAssessmentReportPort).persist(argumentCaptor.capture());

        assertEquals(assessmentResult.getId(), argumentCaptor.getValue().assessmentResultId());
        assertEquals(param.getCurrentUserId(), argumentCaptor.getValue().createdBy());
        assertNotNull(argumentCaptor.getValue().creationTime());

        var locale = Locale.of(assessmentResult.getLanguage().getCode());
        verify(initInsightsHelper).initInsights(assessmentResult, locale);
        verify(regenerateExpiredInsightsHelper).regenerateExpiredInsights(assessmentResult, locale);
    }

    @Test
    void testCreateAssessmentReport_whenParamsAreValidAndReportAlreadyExists_thenInitOrGenerateInsightsWithoutCreatingReport() {
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_QUICK_ASSESSMENT_REPORT))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadAssessmentReportPort.load(param.getAssessmentId())).thenReturn(Optional.of(AssessmentReportMother.empty()));

        service.create(param);

        var locale = Locale.of(assessmentResult.getLanguage().getCode());
        verify(initInsightsHelper).initInsights(assessmentResult, locale);
        verify(regenerateExpiredInsightsHelper).regenerateExpiredInsights(assessmentResult, locale);

        verifyNoInteractions(createAssessmentReportPort);
    }

    private CreateQuickAssessmentReportUseCase.Param createParam(Consumer<CreateQuickAssessmentReportUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private CreateQuickAssessmentReportUseCase.Param.ParamBuilder paramBuilder() {
        return CreateQuickAssessmentReportUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .currentUserId(UUID.randomUUID());
    }
}
