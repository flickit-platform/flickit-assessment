package org.flickit.assessment.core.application.service.assessmentreport;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.assessment.AssessmentPermission;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.port.in.assessmentreport.GetAssessmentReportMetaDataUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAssessmentReportMetaDataServiceTest {

    @InjectMocks
    private GetAssessmentReportMetaDataService service;

    @Mock
    AssessmentAccessChecker assessmentAccessChecker;

    @Test
    void testAssessmentReportMetaData_UserDoesNotHaveEnoughAccess_AccessDeniedException() {
        var param = createParam(GetAssessmentReportMetaDataUseCase.Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), AssessmentPermission.VIEW_ASSESSMENT_REPORT))
            .thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.getAssessmentReportMetaData(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());
    }

    private GetAssessmentReportMetaDataUseCase.Param createParam(Consumer<GetAssessmentReportMetaDataUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private GetAssessmentReportMetaDataUseCase.Param.ParamBuilder paramBuilder() {
        return GetAssessmentReportMetaDataUseCase.Param.builder()
            .assessmentReportId(UUID.randomUUID())
            .assessmentId(UUID.randomUUID())
            .currentUserId(UUID.randomUUID());
    }
}
