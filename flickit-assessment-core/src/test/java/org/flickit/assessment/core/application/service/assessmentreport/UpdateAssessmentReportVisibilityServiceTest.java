package org.flickit.assessment.core.application.service.assessmentreport;

import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.VisibilityType;
import org.flickit.assessment.core.application.port.in.assessmentreport.UpdateAssessmentReportVisibilityUseCase;
import org.flickit.assessment.core.application.port.out.assessmentreport.UpdateAssessmentReportPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
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

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_ASSESSMENT_RESULT_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateAssessmentReportVisibilityServiceTest {

    @InjectMocks
    private UpdateAssessmentReportVisibilityService service;

    @Mock
    private LoadAssessmentResultPort loadAssessmentResultPort;

    @Mock
    private UpdateAssessmentReportPort updateAssessmentReportPort;

    @Test
    void testUpdateAssessmentReportVisibility_whenAssessmentResultDoesNotExist_thenThrowsException() {
        var param = createParam(UpdateAssessmentReportVisibilityUseCase.Param.ParamBuilder::build);

        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId()))
            .thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.updateReportVisibility(param));
        assertEquals(COMMON_ASSESSMENT_RESULT_NOT_FOUND, throwable.getMessage());

        verifyNoInteractions(updateAssessmentReportPort);
    }

    @Test
    void testUpdateAssessmentReportVisibility_whenParametersAreValid_thenThrowsException() {
        var param = createParam(UpdateAssessmentReportVisibilityUseCase.Param.ParamBuilder::build);
        var assessmentResult = AssessmentResultMother.validResult();

        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId()))
            .thenReturn(Optional.of(assessmentResult));
        ArgumentCaptor<UpdateAssessmentReportPort.UpdateVisibilityParam> argumentCaptor = ArgumentCaptor.forClass(UpdateAssessmentReportPort.UpdateVisibilityParam.class);
        service.updateReportVisibility(param);

        verify(updateAssessmentReportPort).updateVisibility(argumentCaptor.capture());

        assertEquals(assessmentResult.getId(), argumentCaptor.getValue().assessmentResultId());
        assertEquals(VisibilityType.RESTRICTED, argumentCaptor.getValue().visibility());
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
