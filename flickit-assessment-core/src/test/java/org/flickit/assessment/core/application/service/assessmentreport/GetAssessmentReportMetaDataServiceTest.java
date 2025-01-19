package org.flickit.assessment.core.application.service.assessmentreport;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.assessment.AssessmentPermission;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.port.in.assessmentreport.GetAssessmentReportMetaDataUseCase;
import org.flickit.assessment.core.application.port.out.assessmentreport.LoadAssessmentReportMetaDataPort;
import org.flickit.assessment.core.test.fixture.application.AssessmentReportMetadataMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAssessmentReportMetaDataServiceTest {

    @InjectMocks
    private GetAssessmentReportMetaDataService service;

    @Mock
    AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    LoadAssessmentReportMetaDataPort loadAssessmentReportMetaDataPort;

    @Test
    void testAssessmentReportMetaData_UserDoesNotHaveEnoughAccess_AccessDeniedException() {
        var param = createParam(GetAssessmentReportMetaDataUseCase.Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), AssessmentPermission.MANAGE_REPORT_METADATA))
            .thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.getAssessmentReportMetaData(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());
    }

    @Test
    void testAssessmentReportMetaData_AssessmentReportDoesNotExists_SuccessfulEmptyAssessmentReport() {
        var param = createParam(GetAssessmentReportMetaDataUseCase.Param.ParamBuilder::build);
        var assessmentReportMetadata = AssessmentReportMetadataMother.createEmptyMetadata();

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), AssessmentPermission.MANAGE_REPORT_METADATA))
            .thenReturn(true);
        when(loadAssessmentReportMetaDataPort.loadMetadata(param.getAssessmentId())).thenReturn(assessmentReportMetadata);

        var result = service.getAssessmentReportMetaData(param);
        assertNull(result.intro());
        assertNull(result.prosAndCons());
        assertNull(result.steps());
        assertNull(result.participants());
    }

    @Test
    void testAssessmentReportMetaData_AssessmentReportExists_SuccessfulEmptyAssessmentReport() {
        var param = createParam(GetAssessmentReportMetaDataUseCase.Param.ParamBuilder::build);
        var assessmentReportMetadata = AssessmentReportMetadataMother.createSimpleMetadata();

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), AssessmentPermission.MANAGE_REPORT_METADATA))
            .thenReturn(true);
        when(loadAssessmentReportMetaDataPort.loadMetadata(param.getAssessmentId())).thenReturn(assessmentReportMetadata);

        var result = service.getAssessmentReportMetaData(param);
        assertEquals(assessmentReportMetadata.getIntro(), result.intro());
        assertEquals(assessmentReportMetadata.getProsAndCons(), result.prosAndCons());
        assertEquals(assessmentReportMetadata.getSteps(), result.steps());
        assertEquals(assessmentReportMetadata.getParticipants(), result.participants());
    }

    private GetAssessmentReportMetaDataUseCase.Param createParam(Consumer<GetAssessmentReportMetaDataUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private GetAssessmentReportMetaDataUseCase.Param.ParamBuilder paramBuilder() {
        return GetAssessmentReportMetaDataUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .currentUserId(UUID.randomUUID());
    }
}
