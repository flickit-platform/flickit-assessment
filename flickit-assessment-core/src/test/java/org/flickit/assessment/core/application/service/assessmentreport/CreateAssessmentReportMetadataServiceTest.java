package org.flickit.assessment.core.application.service.assessmentreport;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AssessmentReport;
import org.flickit.assessment.core.application.domain.AssessmentReportMetadata;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.port.in.assessmentreport.CreateAssessmentReportMetadataUseCase;
import org.flickit.assessment.core.application.port.in.assessmentreport.CreateAssessmentReportMetadataUseCase.MetadataParam.MetadataParamBuilder;
import org.flickit.assessment.core.application.port.in.assessmentreport.CreateAssessmentReportMetadataUseCase.Param;
import org.flickit.assessment.core.application.port.in.assessmentreport.CreateAssessmentReportMetadataUseCase.Param.ParamBuilder;
import org.flickit.assessment.core.application.port.out.assessmentreport.CreateAssessmentReportPort;
import org.flickit.assessment.core.application.port.out.assessmentreport.LoadAssessmentReportPort;
import org.flickit.assessment.core.application.port.out.assessmentreport.UpdateAssessmentReportPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.MANAGE_REPORT_METADATA;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.test.fixture.application.AssessmentReportMother.reportWithMetadata;
import static org.flickit.assessment.core.test.fixture.application.AssessmentResultMother.validResult;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateAssessmentReportMetadataServiceTest {

    @InjectMocks
    private CreateAssessmentReportMetadataService service;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private LoadAssessmentResultPort loadAssessmentResultPort;

    @Mock
    private LoadAssessmentReportPort loadAssessmentReportPort;

    @Mock
    private CreateAssessmentReportPort createAssessmentReportPort;

    @Mock
    private UpdateAssessmentReportPort updateAssessmentReportPort;

    private final AssessmentResult assessmentResult = validResult();

    @Test
    void testCreateAssessmentReportMetadata_whenCurrentUserDoesNotHaveRequiredPermission_thenThrowAccessDeniedException() {
        var param = createParam(ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), MANAGE_REPORT_METADATA))
            .thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.createReportMetadata(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(loadAssessmentResultPort, loadAssessmentReportPort, updateAssessmentReportPort, createAssessmentReportPort);
    }

    @Test
    void testCreateAssessmentReportMetadata_whenAssessmentResultDoesNotExist_thenThrowResourceNotFoundException() {
        var param = createParam(ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), MANAGE_REPORT_METADATA))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.createReportMetadata(param));
        assertEquals(COMMON_ASSESSMENT_RESULT_NOT_FOUND, throwable.getMessage());

        verifyNoInteractions(loadAssessmentReportPort, updateAssessmentReportPort, createAssessmentReportPort);
    }

    @Test
    void testCreateAssessmentReportMetadata_whenAssessmentReportDoesNotExist_thenPersistAssessmentReport() {
        var param = createParam(ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), MANAGE_REPORT_METADATA))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadAssessmentReportPort.load(param.getAssessmentId())).thenReturn(Optional.empty());

        service.createReportMetadata(param);

        ArgumentCaptor<AssessmentReport> assessmentReportParam = ArgumentCaptor.forClass(AssessmentReport.class);
        verify(createAssessmentReportPort, times(1)).persist(assessmentReportParam.capture());

        assertEquals(assessmentResult.getId(), assessmentReportParam.getValue().getAssessmentResultId());
        var actualMetadata = assessmentReportParam.getValue().getMetadata();
        assertNotNull(actualMetadata);
        assertEquals(param.getMetadata().getIntro(), actualMetadata.intro());
        assertNull(actualMetadata.prosAndCons());
        assertNull(param.getMetadata().getSteps(), actualMetadata.steps());
        assertNull(param.getMetadata().getParticipants(), actualMetadata.participants());
        assertFalse(assessmentReportParam.getValue().isPublished());
        assertNotNull(assessmentReportParam.getValue().getLinkHash());
        assertNotNull(assessmentReportParam.getValue().getCreationTime());
        assertNotNull(assessmentReportParam.getValue().getLastModificationTime());
        assertEquals(param.getCurrentUserId(), assessmentReportParam.getValue().getCreatedBy());
        assertEquals(param.getCurrentUserId(), assessmentReportParam.getValue().getLastModifiedBy());

        verifyNoInteractions(updateAssessmentReportPort);
    }

    @Test
    void testCreateAssessmentReportMetadata_whenAssessmentReportExists_thenUpdateAssessmentReport() {
        var param = createParam(ParamBuilder::build);
        var oldMetadata = new AssessmentReportMetadata(null, "pros and cons", null, null);
        var assessmentReport = reportWithMetadata(oldMetadata);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), MANAGE_REPORT_METADATA))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadAssessmentReportPort.load(param.getAssessmentId())).thenReturn(Optional.of(assessmentReport));

        service.createReportMetadata(param);
        ArgumentCaptor<UpdateAssessmentReportPort.UpdateMetadataParam> updateAssessmentReportPortParam = ArgumentCaptor.forClass(UpdateAssessmentReportPort.UpdateMetadataParam.class);
        verify(updateAssessmentReportPort, times(1)).updateMetadata(updateAssessmentReportPortParam.capture());

        assertEquals(assessmentReport.getId(), updateAssessmentReportPortParam.getValue().id());
        assertEquals(param.getMetadata().getIntro(), updateAssessmentReportPortParam.getValue().reportMetadata().intro());
        assertEquals(oldMetadata.prosAndCons(), updateAssessmentReportPortParam.getValue().reportMetadata().prosAndCons());
        assertNull(updateAssessmentReportPortParam.getValue().reportMetadata().steps());
        assertNull(updateAssessmentReportPortParam.getValue().reportMetadata().participants());
        assertNotNull(updateAssessmentReportPortParam.getValue().lastModificationTime());
        assertEquals(param.getCurrentUserId(), updateAssessmentReportPortParam.getValue().lastModifiedBy());

        verifyNoInteractions(createAssessmentReportPort);
    }

    private Param createParam(Consumer<ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private ParamBuilder paramBuilder() {
        return Param.builder()
            .assessmentId(UUID.randomUUID())
            .metadata(metadataParamBuilder().build())
            .currentUserId(UUID.randomUUID());
    }

    private MetadataParamBuilder metadataParamBuilder() {
        return CreateAssessmentReportMetadataUseCase.MetadataParam.builder()
            .intro("intro")
            .prosAndCons(null)
            .steps(null)
            .participants(null);
    }
}
