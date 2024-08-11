package org.flickit.assessment.core.application.service.attribute;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.assessment.AssessmentPermission;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.port.in.attribute.UpdateAttributeInsightUseCase;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.attributeinsight.LoadAttributeInsightPort;
import org.flickit.assessment.core.application.port.out.attributeinsight.UpdateAttributeInsightPort;
import org.flickit.assessment.core.test.fixture.application.AssessmentResultMother;
import org.flickit.assessment.core.test.fixture.application.AttributeInsightMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.UPDATE_ATTRIBUTE_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.core.common.ErrorMessageKey.UPDATE_ATTRIBUTE_INSIGHT_ATTRIBUTE_INSIGHT_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateAttributeInsightServiceTest {

    @InjectMocks
    private UpdateAttributeInsightService service;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private LoadAttributeInsightPort loadAttributeInsightPort;

    @Mock
    private LoadAssessmentResultPort assessmentResultPort;

    @Mock
    private ValidateAssessmentResultPort validateAssessmentResultPort;

    @Mock
    private UpdateAttributeInsightPort updateAttributeInsightPort;

    @Test
    void updateAssessmentAttributeInsight_NotEnoughPermission_AccessDeniedExceptionError() {
        var attributeId = 1L;
        var content = "content";
        var currentUserId = UUID.randomUUID();
        var param = new UpdateAttributeInsightUseCase.Param(UUID.randomUUID(), attributeId, content, currentUserId);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), currentUserId, AssessmentPermission.EXPORT_ASSESSMENT_REPORT)).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, ()-> service.updateAttributeInsight(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(loadAttributeInsightPort ,
            assessmentResultPort,
            validateAssessmentResultPort,
            updateAttributeInsightPort);
    }

    @Test
    void updateAssessmentAttributeInsight_AssessmentResultNotExist_ResourceNotFoundError() {
        var attributeId = 1L;
        var content = "content";
        var currentUserId = UUID.randomUUID();
        var param = new UpdateAttributeInsightUseCase.Param(UUID.randomUUID(), attributeId, content, currentUserId);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), currentUserId, AssessmentPermission.EXPORT_ASSESSMENT_REPORT)).thenReturn(true);
        when(assessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.updateAttributeInsight(param));
        assertEquals(UPDATE_ATTRIBUTE_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND, throwable.getMessage());

        verifyNoInteractions(loadAttributeInsightPort,
            validateAssessmentResultPort,
            updateAttributeInsightPort);
    }

    @Test
    void updateAssessmentAttributeInsight_AttributeInsightNotExist_ResourceNotFoundError() {
        var attributeId = 1L;
        var content = "content";
        var currentUserId = UUID.randomUUID();
        var param = new UpdateAttributeInsightUseCase.Param(UUID.randomUUID(), attributeId, content, currentUserId);
        var assessmentResult = AssessmentResultMother.validResultWithJustAnId();

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), currentUserId, AssessmentPermission.EXPORT_ASSESSMENT_REPORT)).thenReturn(true);
        when(assessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        doNothing().when(validateAssessmentResultPort).validate(param.getAssessmentId());
        when(loadAttributeInsightPort.loadAttributeAiInsight(assessmentResult.getId(), attributeId)).thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.updateAttributeInsight(param));
        assertEquals(UPDATE_ATTRIBUTE_INSIGHT_ATTRIBUTE_INSIGHT_NOT_FOUND, throwable.getMessage());

        verifyNoInteractions(updateAttributeInsightPort);
    }

    @Test
    void updateAssessmentAttributeInsight_validParameters_SuccessfulUpdate() {
        var attributeId = 1L;
        var content = "content";
        var currentUserId = UUID.randomUUID();
        var param = new UpdateAttributeInsightUseCase.Param(UUID.randomUUID(), attributeId, content, currentUserId);
        var assessmentResult = AssessmentResultMother.validResultWithJustAnId();
        var attributeInsight = AttributeInsightMother.simpleAttributeAiInsight();

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), currentUserId, AssessmentPermission.EXPORT_ASSESSMENT_REPORT)).thenReturn(true);
        when(assessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        doNothing().when(validateAssessmentResultPort).validate(param.getAssessmentId());
        when(loadAttributeInsightPort.loadAttributeAiInsight(assessmentResult.getId(), attributeId)).thenReturn(Optional.of(attributeInsight));

        assertDoesNotThrow(() -> service.updateAttributeInsight(param));
    }
}
