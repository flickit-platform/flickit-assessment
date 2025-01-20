package org.flickit.assessment.core.application.service.assessmentreport;

import lombok.SneakyThrows;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.assessment.AssessmentPermission;
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
    void testAssessmentReportMetadata_UserDoesNotHaveEnoughAccess_AccessDeniedException() {
        var param = createParam(GetAssessmentReportMetadataUseCase.Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), AssessmentPermission.MANAGE_REPORT_METADATA))
            .thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.getAssessmentReportMetadata(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());
    }

    @Test
    void testAssessmentReportMetadata_AssessmentReportDoesNotExists_SuccessfulEmptyAssessmentReport() {
        var param = createParam(GetAssessmentReportMetadataUseCase.Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), AssessmentPermission.MANAGE_REPORT_METADATA))
            .thenReturn(true);
        when(loadAssessmentReportPort.load(param.getAssessmentId())).thenReturn(Optional.empty());

        var result = service.getAssessmentReportMetadata(param);
        assertNull(result.intro());
        assertNull(result.prosAndCons());
        assertNull(result.steps());
        assertNull(result.participants());
    }

    @SneakyThrows
    @Test
    void testAssessmentReportMetadata_AssessmentReportExists_ReturnsFullMetadata() {
        var param = createParam(GetAssessmentReportMetadataUseCase.Param.ParamBuilder::build);
        var metadata = new AssessmentReportMetadata("introduction of assessment report",
            "pros and cons of assessment",
            "description of steps taken to perform the assessment",
            "participants\": \"list of assessment participants and their participation's");
        var assessmentReport = AssessmentReportMother.reportWithMetadata(metadata);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), AssessmentPermission.MANAGE_REPORT_METADATA))
            .thenReturn(true);
        when(loadAssessmentReportPort.load(param.getAssessmentId())).thenReturn(Optional.of(assessmentReport));

        var result = service.getAssessmentReportMetadata(param);
        assertEquals(metadata.intro(), result.intro());
        assertEquals(metadata.prosAndCons(), result.prosAndCons());
        assertEquals(metadata.steps(), result.steps());
        assertEquals(metadata.participants(), result.participants());
    }

    @SneakyThrows
    @Test
    void testAssessmentReportMetadata_AssessmentReportExistsSomeKeysAreNull_ReturnsPartialMetadata() {
        var param = createParam(GetAssessmentReportMetadataUseCase.Param.ParamBuilder::build);
        var metadata = new AssessmentReportMetadata("introduction of assessment report",
            "pros and cons of assessment",
            null,
            null);
        var assessmentReport = AssessmentReportMother.reportWithMetadata(metadata);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), AssessmentPermission.MANAGE_REPORT_METADATA))
            .thenReturn(true);
        when(loadAssessmentReportPort.load(param.getAssessmentId())).thenReturn(Optional.of(assessmentReport));

        var result = service.getAssessmentReportMetadata(param);
        assertEquals(metadata.intro(), result.intro());
        assertEquals(metadata.prosAndCons(), result.prosAndCons());
        assertNull(result.steps());
        assertNull(result.participants());
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
