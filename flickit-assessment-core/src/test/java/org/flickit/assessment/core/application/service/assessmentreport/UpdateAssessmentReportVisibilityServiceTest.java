package org.flickit.assessment.core.application.service.assessmentreport;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.InvalidStateException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AssessmentReport;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.domain.VisibilityType;
import org.flickit.assessment.core.application.port.in.assessmentreport.UpdateAssessmentReportVisibilityUseCase;
import org.flickit.assessment.core.application.port.out.assessmentreport.LoadAssessmentReportPort;
import org.flickit.assessment.core.application.port.out.assessmentreport.UpdateAssessmentReportPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.test.fixture.application.AssessmentReportMother;
import org.flickit.assessment.core.test.fixture.application.AssessmentResultMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.MANAGE_ASSESSMENT_REPORT_VISIBILITY;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.common.exception.api.ErrorCodes.REPORT_UNPUBLISHED;
import static org.flickit.assessment.core.common.ErrorMessageKey.UPDATE_ASSESSMENT_REPORT_VISIBILITY_ASSESSMENT_REPORT_NOT_FOUND;
import static org.flickit.assessment.core.common.ErrorMessageKey.UPDATE_ASSESSMENT_REPORT_VISIBILITY_ASSESSMENT_REPORT_NOT_PUBLISHED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateAssessmentReportVisibilityServiceTest {

    @InjectMocks
    private UpdateAssessmentReportVisibilityService service;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private LoadAssessmentResultPort loadAssessmentResultPort;

    @Mock
    private UpdateAssessmentReportPort updateAssessmentReportPort;

    @Mock
    private LoadAssessmentReportPort loadAssessmentReportPort;

    private final AssessmentResult assessmentResult = AssessmentResultMother.validResult();
    private UpdateAssessmentReportVisibilityUseCase.Param param = createParam(UpdateAssessmentReportVisibilityUseCase.Param.ParamBuilder::build);
    private AssessmentReport assessmentReport = AssessmentReportMother.publishedReportWithMetadata(null);

    @Test
    void testUpdateAssessmentReportVisibility_whenCurrentUserDoesNotHaveRequiredPermission_thenThrowsException() {
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), MANAGE_ASSESSMENT_REPORT_VISIBILITY))
            .thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.updateReportVisibility(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(updateAssessmentReportPort, loadAssessmentReportPort);
    }

    @Test
    void testUpdateAssessmentReportVisibility_whenAssessmentResultDoesNotExist_thenThrowsException() {
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), MANAGE_ASSESSMENT_REPORT_VISIBILITY))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId()))
            .thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.updateReportVisibility(param));
        assertEquals(COMMON_ASSESSMENT_RESULT_NOT_FOUND, throwable.getMessage());

        verifyNoInteractions(updateAssessmentReportPort, loadAssessmentReportPort);
    }

    @Test
    void testUpdateAssessmentReportVisibility_whenAssessmentReportDoesNotExist_thenThrowsException() {
        param = createParam(b -> b.visibility("PUBLIC"));

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), MANAGE_ASSESSMENT_REPORT_VISIBILITY))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId()))
            .thenReturn(Optional.of(AssessmentResultMother.validResult()));
        when(loadAssessmentReportPort.load(param.getAssessmentId()))
            .thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.updateReportVisibility(param));
        assertEquals(UPDATE_ASSESSMENT_REPORT_VISIBILITY_ASSESSMENT_REPORT_NOT_FOUND, throwable.getMessage());

        verifyNoInteractions(updateAssessmentReportPort);
    }

    @Test
    void testUpdateAssessmentReportVisibility_whenAssessmentReportIsNotPublished_thenThrowsException() {
        param = createParam(b -> b.visibility("PUBLIC"));
        assessmentReport = AssessmentReportMother.empty();

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), MANAGE_ASSESSMENT_REPORT_VISIBILITY))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId()))
            .thenReturn(Optional.of(AssessmentResultMother.validResult()));
        when(loadAssessmentReportPort.load(param.getAssessmentId()))
            .thenReturn(Optional.of(assessmentReport));

        var throwable = assertThrows(InvalidStateException.class, () -> service.updateReportVisibility(param));
        assertEquals(REPORT_UNPUBLISHED, throwable.getCode());
        assertEquals(UPDATE_ASSESSMENT_REPORT_VISIBILITY_ASSESSMENT_REPORT_NOT_PUBLISHED, throwable.getMessage());

        verifyNoInteractions(updateAssessmentReportPort);
    }

    @Test
    void testUpdateAssessmentReportVisibility_whenVisibilityParamIsRestricted_thenSuccessfulUpdateWithoutLinkHash() {
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), MANAGE_ASSESSMENT_REPORT_VISIBILITY))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId()))
            .thenReturn(Optional.of(assessmentResult));
        when(loadAssessmentReportPort.load(param.getAssessmentId()))
            .thenReturn(Optional.of(assessmentReport));

        ArgumentCaptor<UpdateAssessmentReportPort.UpdateVisibilityParam> argumentCaptor = ArgumentCaptor.forClass(UpdateAssessmentReportPort.UpdateVisibilityParam.class);
        service.updateReportVisibility(param);

        verify(updateAssessmentReportPort).updateVisibilityStatus(argumentCaptor.capture());

        assertEquals(assessmentResult.getId(), argumentCaptor.getValue().assessmentResultId());
        assertEquals(VisibilityType.RESTRICTED, argumentCaptor.getValue().visibility());
        assertNotNull(argumentCaptor.getValue().lastModificationTime());
        assertEquals(param.getCurrentUserId(), argumentCaptor.getValue().lastModifiedBy());
    }

    @Test
    void testUpdateAssessmentReportVisibility_whenVisibilityParamIsPublic_thenSuccessfulUpdateWithLinkHash() {
        param = createParam(b -> b.visibility("PUBLIC"));

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), MANAGE_ASSESSMENT_REPORT_VISIBILITY))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId()))
            .thenReturn(Optional.of(assessmentResult));
        when(loadAssessmentReportPort.load(param.getAssessmentId()))
            .thenReturn(Optional.of(assessmentReport));
        ArgumentCaptor<UpdateAssessmentReportPort.UpdateVisibilityParam> argumentCaptor = ArgumentCaptor.forClass(UpdateAssessmentReportPort.UpdateVisibilityParam.class);
        service.updateReportVisibility(param);

        verify(updateAssessmentReportPort).updateVisibilityStatus(argumentCaptor.capture());

        assertEquals(assessmentResult.getId(), argumentCaptor.getValue().assessmentResultId());
        assertEquals(VisibilityType.PUBLIC, argumentCaptor.getValue().visibility());
        assertNotNull(argumentCaptor.getValue().lastModificationTime());
        assertEquals(param.getCurrentUserId(), argumentCaptor.getValue().lastModifiedBy());
    }


    private UpdateAssessmentReportVisibilityUseCase.Param createParam(Consumer<UpdateAssessmentReportVisibilityUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private UpdateAssessmentReportVisibilityUseCase.Param.ParamBuilder paramBuilder() {
        return UpdateAssessmentReportVisibilityUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .visibility(VisibilityType.RESTRICTED.name())
            .currentUserId(UUID.randomUUID());
    }
}
