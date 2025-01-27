package org.flickit.assessment.core.application.service.assessmentreport;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.domain.AssessmentReportMetadata;
import org.flickit.assessment.core.application.port.in.assessmentreport.GetAssessmentReportMetadataUseCase;
import org.flickit.assessment.core.application.port.out.assessmentreport.LoadAssessmentReportPort;
import org.flickit.assessment.core.test.fixture.application.AssessmentReportMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.MANAGE_REPORT_METADATA;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAssessmentReportMetadataServiceTest {

    @InjectMocks
    private GetAssessmentReportMetadataService service;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private LoadAssessmentReportPort loadAssessmentReportPort;

    @Test
    void testAssessmentReportMetadata_whenCurrentUserDoesNotHaveRequiredPermission_thenThrowAccessDeniedException() {
        var param = createParam(GetAssessmentReportMetadataUseCase.Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), MANAGE_REPORT_METADATA))
            .thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.getAssessmentReportMetadata(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());
    }

    @Test
    void testAssessmentReportMetadata_whenAssessmentReportDoesNotExist_thenReturnEmptyMetadata() {
        var param = createParam(GetAssessmentReportMetadataUseCase.Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), MANAGE_REPORT_METADATA))
            .thenReturn(true);
        when(loadAssessmentReportPort.load(param.getAssessmentId())).thenReturn(Optional.empty());

        var result = service.getAssessmentReportMetadata(param);
        assertNull(result.metadata().intro());
        assertNull(result.metadata().prosAndCons());
        assertNull(result.metadata().steps());
        assertNull(result.metadata().participants());
        assertFalse(result.published());
    }

    @Test
    void testAssessmentReportMetadata_whenAssessmentReportExists_thenReturnMetadata() {
        var param = createParam(GetAssessmentReportMetadataUseCase.Param.ParamBuilder::build);
        var metadata = new AssessmentReportMetadata("introduction of assessment report",
            "pros and cons of assessment",
            null,
            "list of assessment participants and their participation's");
        var assessmentReport = AssessmentReportMother.reportWithMetadata(metadata);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), MANAGE_REPORT_METADATA))
            .thenReturn(true);
        when(loadAssessmentReportPort.load(param.getAssessmentId())).thenReturn(Optional.of(assessmentReport));

        var result = service.getAssessmentReportMetadata(param);
        assertEquals(metadata.intro(), result.metadata().intro());
        assertEquals(metadata.prosAndCons(), result.metadata().prosAndCons());
        assertNull(result.metadata().steps());
        assertEquals(metadata.participants(), result.metadata().participants());
        assertEquals(assessmentReport.isPublished(), result.published());
    }

    private GetAssessmentReportMetadataUseCase.Param createParam(Consumer<GetAssessmentReportMetadataUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private GetAssessmentReportMetadataUseCase.Param.ParamBuilder paramBuilder() {
        return GetAssessmentReportMetadataUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .currentUserId(UUID.randomUUID());
    }
}
