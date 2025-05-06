package org.flickit.assessment.core.application.service.assessmentreport;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.VisibilityType;
import org.flickit.assessment.core.application.port.in.assessmentreport.UpdateAssessmentReportPublishStatusUseCase.Param;
import org.flickit.assessment.core.application.port.out.assessmentreport.CreateAssessmentReportPort;
import org.flickit.assessment.core.application.port.out.assessmentreport.LoadAssessmentReportPort;
import org.flickit.assessment.core.application.port.out.assessmentreport.UpdateAssessmentReportPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.test.fixture.application.AssessmentReportMother;
import org.flickit.assessment.core.test.fixture.application.AssessmentResultMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.PUBLISH_ASSESSMENT_REPORT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateAssessmentReportPublishStatusServiceTest {

    @InjectMocks
    private UpdateAssessmentReportPublishStatusService service;

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

    @Captor
    ArgumentCaptor<UpdateAssessmentReportPort.UpdatePublishParam> updatePublishPortParam;

    @Captor
    ArgumentCaptor<CreateAssessmentReportPort.Param> assessmentReportCaptor;

    @Test
    void testUpdateAssessmentReportPublishStatus_whenCurrentUserDoesNotHaveRequiredPermission_thenThrowAccessDeniedException() {
        var param = createParam(Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), PUBLISH_ASSESSMENT_REPORT))
            .thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.updateReportPublishStatus(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(loadAssessmentResultPort,
            loadAssessmentReportPort,
            createAssessmentReportPort,
            updateAssessmentReportPort);
    }

    @Test
    void testUpdateAssessmentReportPublishStatus_whenAssessmentResultDoesNotExist_thenThrowResourceNotFoundException() {
        var param = createParam(Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), PUBLISH_ASSESSMENT_REPORT))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.updateReportPublishStatus(param));
        assertEquals(COMMON_ASSESSMENT_RESULT_NOT_FOUND, throwable.getMessage());

        verifyNoInteractions(updateAssessmentReportPort,
            createAssessmentReportPort,
            updateAssessmentReportPort);
    }

    @Test
    void testUpdateAssessmentReportPublishStatus_whenAssessmentReportExists_thenPublishAssessmentReport() {
        var param = createParam(b -> b.published(Boolean.TRUE));
        var assessmentResult = AssessmentResultMother.validResult();
        var assessmentReport = AssessmentReportMother.empty();

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), PUBLISH_ASSESSMENT_REPORT))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadAssessmentReportPort.load(param.getAssessmentId())).thenReturn(Optional.of(assessmentReport));

        service.updateReportPublishStatus(param);

        verify(updateAssessmentReportPort, times(1)).updatePublishStatus(updatePublishPortParam.capture());

        assertEquals(assessmentResult.getId(), updatePublishPortParam.getValue().assessmentResultId());
        assertNotNull(updatePublishPortParam.getValue().lastModificationTime());
        assertEquals(param.getCurrentUserId(), updatePublishPortParam.getValue().lastModifiedBy());
        assertEquals(VisibilityType.RESTRICTED, updatePublishPortParam.getValue().visibilityType());
        assertEquals(assessmentResult.getId(), updatePublishPortParam.getValue().assessmentResultId());
        assertTrue(updatePublishPortParam.getValue().published());
        assertEquals(param.getCurrentUserId(), updatePublishPortParam.getValue().lastModifiedBy());
        assertNotNull(updatePublishPortParam.getValue().lastModificationTime());

        verifyNoInteractions(createAssessmentReportPort);
    }

    @Test
    void testUpdateAssessmentReportPublishStatus_whenAssessmentReportNotExists_thenPublishAssessmentReport() {
        var param = createParam(b -> b.published(Boolean.TRUE));
        var assessmentResult = AssessmentResultMother.validResult();

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), PUBLISH_ASSESSMENT_REPORT))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadAssessmentReportPort.load(param.getAssessmentId())).thenReturn(Optional.empty());

        service.updateReportPublishStatus(param);

        verify(updateAssessmentReportPort, times(1)).updatePublishStatus(updatePublishPortParam.capture());
        verify(createAssessmentReportPort, times(1)).persist(assessmentReportCaptor.capture());

        assertEquals(assessmentResult.getId(), assessmentReportCaptor.getValue().assessmentResultId());
        assertFalse(assessmentReportCaptor.getValue().published());
        assertNotNull(assessmentReportCaptor.getValue().lastModificationTime());
        assertEquals(param.getCurrentUserId(), assessmentReportCaptor.getValue().lastModifiedBy());
        assertNotNull(assessmentReportCaptor.getValue().creationTime());
        assertEquals(param.getCurrentUserId(), assessmentReportCaptor.getValue().createdBy());
        assertNotNull(assessmentReportCaptor.getValue().creationTime());
        assertEquals(VisibilityType.RESTRICTED, assessmentReportCaptor.getValue().visibility());

        assertEquals(assessmentResult.getId(), updatePublishPortParam.getValue().assessmentResultId());
        assertNotNull(updatePublishPortParam.getValue().lastModificationTime());
        assertEquals(param.getCurrentUserId(), updatePublishPortParam.getValue().lastModifiedBy());
        assertEquals(VisibilityType.RESTRICTED, updatePublishPortParam.getValue().visibilityType());
        assertEquals(assessmentResult.getId(), updatePublishPortParam.getValue().assessmentResultId());
        assertTrue(updatePublishPortParam.getValue().published());
        assertEquals(param.getCurrentUserId(), updatePublishPortParam.getValue().lastModifiedBy());
        assertNotNull(updatePublishPortParam.getValue().lastModificationTime());
    }

    @Test
    void testUpdateAssessmentReportPublishStatus_whenUnpublishAssessmentReport_thenUnpublishAssessmentReport() {
        var param = createParam(b -> b.published(Boolean.FALSE));
        var assessmentResult = AssessmentResultMother.validResult();
        var assessmentReport = AssessmentReportMother.withVisibility(VisibilityType.RESTRICTED);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), PUBLISH_ASSESSMENT_REPORT))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadAssessmentReportPort.load(param.getAssessmentId())).thenReturn(Optional.of(assessmentReport));

        service.updateReportPublishStatus(param);

        verify(updateAssessmentReportPort, times(1)).updatePublishStatus(updatePublishPortParam.capture());

        assertEquals(assessmentResult.getId(), updatePublishPortParam.getValue().assessmentResultId());
        assertNotNull(updatePublishPortParam.getValue().lastModificationTime());
        assertEquals(param.getCurrentUserId(), updatePublishPortParam.getValue().lastModifiedBy());
        assertEquals(VisibilityType.RESTRICTED, updatePublishPortParam.getValue().visibilityType());
        assertEquals(assessmentResult.getId(), updatePublishPortParam.getValue().assessmentResultId());
        assertFalse(updatePublishPortParam.getValue().published());
        assertEquals(param.getCurrentUserId(), updatePublishPortParam.getValue().lastModifiedBy());
        assertNotNull(updatePublishPortParam.getValue().lastModificationTime());

        verifyNoInteractions(createAssessmentReportPort);
    }

    @Test
    void testUpdateAssessmentReportPublishStatus_whenPublishAlreadyPublishedAssessmentReport_thenPublishAssessmentReport() {
        var param = createParam(b -> b.published(Boolean.TRUE));
        var assessmentResult = AssessmentResultMother.validResult();
        var assessmentReport = AssessmentReportMother.withVisibility(VisibilityType.PUBLIC);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), PUBLISH_ASSESSMENT_REPORT))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadAssessmentReportPort.load(param.getAssessmentId())).thenReturn(Optional.of(assessmentReport));

        service.updateReportPublishStatus(param);

        verify(updateAssessmentReportPort, times(1)).updatePublishStatus(updatePublishPortParam.capture());

        assertEquals(assessmentResult.getId(), updatePublishPortParam.getValue().assessmentResultId());
        assertNotNull(updatePublishPortParam.getValue().lastModificationTime());
        assertEquals(param.getCurrentUserId(), updatePublishPortParam.getValue().lastModifiedBy());
        assertEquals(VisibilityType.RESTRICTED, updatePublishPortParam.getValue().visibilityType());
        assertEquals(assessmentResult.getId(), updatePublishPortParam.getValue().assessmentResultId());
        assertTrue(updatePublishPortParam.getValue().published());
        assertEquals(param.getCurrentUserId(), updatePublishPortParam.getValue().lastModifiedBy());
        assertNotNull(updatePublishPortParam.getValue().lastModificationTime());

        verifyNoInteractions(createAssessmentReportPort);
    }

    private Param createParam(Consumer<Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private Param.ParamBuilder paramBuilder() {
        return Param.builder()
            .assessmentId(UUID.randomUUID())
            .published(Boolean.TRUE)
            .currentUserId(UUID.randomUUID());
    }
}
