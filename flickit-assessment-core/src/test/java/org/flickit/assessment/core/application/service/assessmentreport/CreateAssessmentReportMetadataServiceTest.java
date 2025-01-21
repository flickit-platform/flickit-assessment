package org.flickit.assessment.core.application.service.assessmentreport;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.domain.AssessmentReport;
import org.flickit.assessment.core.application.domain.AssessmentReportMetadata;
import org.flickit.assessment.core.application.port.in.assessmentreport.CreateAssessmentReportMetadataUseCase;
import org.flickit.assessment.core.application.port.in.assessmentreport.CreateAssessmentReportMetadataUseCase.MetadataParam.MetadataParamBuilder;
import org.flickit.assessment.core.application.port.in.assessmentreport.CreateAssessmentReportMetadataUseCase.Param;
import org.flickit.assessment.core.application.port.in.assessmentreport.CreateAssessmentReportMetadataUseCase.Param.ParamBuilder;
import org.flickit.assessment.core.application.port.out.assessmentreport.CreateAssessmentReportPort;
import org.flickit.assessment.core.application.port.out.assessmentreport.LoadAssessmentReportPort;
import org.flickit.assessment.core.application.port.out.assessmentreport.UpdateAssessmentReportPort;
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
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateAssessmentReportMetadataServiceTest {

    @InjectMocks
    private CreateAssessmentReportMetadataService service;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private LoadAssessmentReportPort loadAssessmentReportPort;

    @Mock
    private CreateAssessmentReportPort createAssessmentReportPort;

    @Mock
    private UpdateAssessmentReportPort updateAssessmentReportPort;

    @Test
    void testCreateAssessmentReportMetadata_UserDoesNotHaveEnoughAccess_AccessDeniedException() {
        var param = createParam(ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), MANAGE_REPORT_METADATA))
            .thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.createReportMetadata(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(loadAssessmentReportPort, updateAssessmentReportPort, createAssessmentReportPort);
    }

    @Test
    void testCreateAssessmentReportMetadata_AssessmentReportDoesNotExists_PersistAssessmentReport() {
        var param = createParam(ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), MANAGE_REPORT_METADATA))
            .thenReturn(true);
        when(loadAssessmentReportPort.load(param.getAssessmentId())).thenReturn(Optional.empty());

        service.createReportMetadata(param);
        ArgumentCaptor<AssessmentReportMetadata> assessmentReportParam = ArgumentCaptor.forClass(AssessmentReportMetadata.class);
        ArgumentCaptor<UUID> assessmentIdPortParam = ArgumentCaptor.forClass(UUID.class);
        verify(createAssessmentReportPort, times(1)).persist(assessmentIdPortParam.capture(), assessmentReportParam.capture());

        assertEquals(param.getAssessmentId(), assessmentIdPortParam.getValue());
        assertEquals(param.getMetadata().getIntro(), assessmentReportParam.getValue().intro());
        assertEquals(param.getMetadata().getProsAndCons(), assessmentReportParam.getValue().prosAndCons());
        assertEquals(param.getMetadata().getSteps(), assessmentReportParam.getValue().steps());
        assertEquals(param.getMetadata().getParticipants(), assessmentReportParam.getValue().participants());

        verifyNoInteractions(updateAssessmentReportPort);
    }

    @Test
    void testCreateAssessmentReportMetadata_AssessmentReportExists_UpdateAssessmentReport() {
        var param = createParam(ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), MANAGE_REPORT_METADATA))
            .thenReturn(true);
        var oldMetadata = new AssessmentReportMetadata(null, null, null, null);
        var assessmentReport = new AssessmentReport(UUID.randomUUID(), UUID.randomUUID(), oldMetadata);
        when(loadAssessmentReportPort.load(param.getAssessmentId())).thenReturn(Optional.of(assessmentReport));

        service.createReportMetadata(param);
        ArgumentCaptor<AssessmentReportMetadata> assessmentReportParam = ArgumentCaptor.forClass(AssessmentReportMetadata.class);
        ArgumentCaptor<UUID> reportIdPortParam = ArgumentCaptor.forClass(UUID.class);
        verify(updateAssessmentReportPort, times(1)).update(reportIdPortParam.capture(), assessmentReportParam.capture());

        assertEquals(assessmentReport.getId(), reportIdPortParam.getValue());
        assertEquals(param.getMetadata().getIntro(), assessmentReportParam.getValue().intro());
        assertEquals(param.getMetadata().getProsAndCons(), assessmentReportParam.getValue().prosAndCons());
        assertEquals(param.getMetadata().getSteps(), assessmentReportParam.getValue().steps());
        assertEquals(param.getMetadata().getParticipants(), assessmentReportParam.getValue().participants());

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
            .prosAndCons("prosAndCons")
            .steps("steps")
            .participants("participants");
    }
}