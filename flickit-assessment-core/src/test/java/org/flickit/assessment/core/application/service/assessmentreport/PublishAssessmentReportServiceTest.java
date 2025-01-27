package org.flickit.assessment.core.application.service.assessmentreport;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.port.in.assessmentreport.PublishAssessmentReportUseCase.Param;
import org.flickit.assessment.core.application.port.out.assessmentreport.LoadAssessmentReportPort;
import org.flickit.assessment.core.application.port.out.assessmentreport.PublishAssessmentReportPort;
import org.flickit.assessment.core.test.fixture.application.AssessmentReportMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.PUBLISH_ASSESSMENT_REPORT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.PUBLISH_ASSESSMENT_REPORT_ASSESSMENT_REPORT_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PublishAssessmentReportServiceTest {

    @InjectMocks
    private PublishAssessmentReportService service;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private LoadAssessmentReportPort loadAssessmentReportPort;

    @Mock
    private PublishAssessmentReportPort publishAssessmentReportPort;

    @Test
    void testPublishAssessmentReport_whenCurrentUserDoesNotHaveRequiredPermission_thenThrowAccessDeniedException() {
        var param = createParam(Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), PUBLISH_ASSESSMENT_REPORT))
            .thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.publishAssessmentReport(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(loadAssessmentReportPort, publishAssessmentReportPort);
    }

    @Test
    void testPublishAssessmentReport_whenAssessmentReportDoesNotExist_thenThrowResourceNotFoundException() {
        var param = createParam(Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), PUBLISH_ASSESSMENT_REPORT))
            .thenReturn(true);
        when(loadAssessmentReportPort.load(param.getAssessmentId())).thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.publishAssessmentReport(param));
        assertEquals(PUBLISH_ASSESSMENT_REPORT_ASSESSMENT_REPORT_NOT_FOUND, throwable.getMessage());

        verifyNoInteractions(publishAssessmentReportPort);
    }

    @Test
    void testPublishAssessmentReport_whenAssessmentReportExists_thenPublishAssessmentReport() {
        var param = createParam(Param.ParamBuilder::build);
        var assessmentReport = AssessmentReportMother.reportWithMetadata(null);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), PUBLISH_ASSESSMENT_REPORT))
            .thenReturn(true);
        when(loadAssessmentReportPort.load(param.getAssessmentId()))
            .thenReturn(Optional.of(assessmentReport));
        doNothing().when(publishAssessmentReportPort).publish(any());

        service.publishAssessmentReport(param);

        var publishPortParam = ArgumentCaptor.forClass(PublishAssessmentReportPort.Param.class);
        verify(publishAssessmentReportPort, times(1)).publish(publishPortParam.capture());

        assertEquals(assessmentReport.getId(), publishPortParam.getValue().assessmentReportId());
        assertNotNull(publishPortParam.getValue().lastModificationTime());
        assertEquals(param.getCurrentUserId(), publishPortParam.getValue().lastModifiedBy());
    }

    private Param createParam(Consumer<Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private Param.ParamBuilder paramBuilder() {
        return Param.builder()
            .assessmentId(UUID.randomUUID())
            .currentUserId(UUID.randomUUID());
    }
}
