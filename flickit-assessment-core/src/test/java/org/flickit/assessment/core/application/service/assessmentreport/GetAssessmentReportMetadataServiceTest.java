package org.flickit.assessment.core.application.service.assessmentreport;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.assessment.AssessmentPermission;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.domain.AssessmentReportMetadata;
import org.flickit.assessment.core.application.port.in.assessmentreport.GetAssessmentReportMetadataUseCase;
import org.flickit.assessment.core.application.port.out.assessmentreport.LoadAssessmentReportMetadataPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAssessmentReportMetadataServiceTest {

    @InjectMocks
    private GetAssessmentReportMetadataService service;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private LoadAssessmentReportMetadataPort loadAssessmentReportMetadataPort;

    @Mock
    private ObjectMapper objectMapper;

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
        when(loadAssessmentReportMetadataPort.load(param.getAssessmentId())).thenReturn(null);

        var result = service.getAssessmentReportMetadata(param);
        assertNull(result.intro());
        assertNull(result.prosAndCons());
        assertNull(result.steps());
        assertNull(result.participants());

        verifyNoInteractions(objectMapper);
    }


    @SneakyThrows
    @Test
    void testAssessmentReportMetadata_AssessmentReportExists_SuccessfulEmptyAssessmentReport() {
        var param = createParam(GetAssessmentReportMetadataUseCase.Param.ParamBuilder::build);
        String portResult = "{\"intro\": \"introduction of assessment report\", " +
            "\"prosAndCons\": \"pros and cons of assessment\", " +
            "\"steps\": \"description of steps taken to perform the assessment\", " +
            "\"participants\": \"list of assessment participants and their participation's\"}";
        var expectedMetadata = new ObjectMapper().readValue(portResult, AssessmentReportMetadata.class);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), AssessmentPermission.MANAGE_REPORT_METADATA))
            .thenReturn(true);
        when(loadAssessmentReportMetadataPort.load(param.getAssessmentId())).thenReturn(portResult);
        when(objectMapper.readValue(portResult, AssessmentReportMetadata.class)).thenReturn(expectedMetadata);

        var result = service.getAssessmentReportMetadata(param);
        assertEquals(expectedMetadata.intro(), result.intro());
        assertEquals(expectedMetadata.prosAndCons(), result.prosAndCons());
        assertEquals(expectedMetadata.steps(), result.steps());
        assertEquals(expectedMetadata.participants(), result.participants());
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
