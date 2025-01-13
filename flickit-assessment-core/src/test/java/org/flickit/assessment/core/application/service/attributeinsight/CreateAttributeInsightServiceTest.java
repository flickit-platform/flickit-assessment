package org.flickit.assessment.core.application.service.attributeinsight;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.port.in.attributeinsight.CreateAttributeInsightUseCase;
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

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.CREATE_ATTRIBUTE_INSIGHT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.CREATE_ATTRIBUTE_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.core.common.ErrorMessageKey.CREATE_ATTRIBUTE_INSIGHT_ATTRIBUTE_INSIGHT_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateAttributeInsightServiceTest {

    @InjectMocks
    private CreateAttributeInsightService service;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private LoadAttributeInsightPort loadAttributeInsightPort;

    @Mock
    private LoadAssessmentResultPort assessmentResultPort;

    @Mock
    private UpdateAttributeInsightPort updateAttributeInsightPort;

    @Test
    void createAttributeInsight_UserDoesNotHaveRequiredPermission_ThrowAccessDeniedException() {
        var currentUserId = UUID.randomUUID();
        var param = new CreateAttributeInsightUseCase.Param(UUID.randomUUID(), 1L, "content", currentUserId);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), currentUserId, CREATE_ATTRIBUTE_INSIGHT)).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.createAttributeInsight(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(loadAttributeInsightPort, assessmentResultPort, updateAttributeInsightPort);
    }

    @Test
    void createAttributeInsight_AssessmentResultDoesNotExist_ThrowResourceNotFoundException() {
        var content = "content";
        var currentUserId = UUID.randomUUID();
        var param = new CreateAttributeInsightUseCase.Param(UUID.randomUUID(), 123L, content, currentUserId);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), currentUserId, CREATE_ATTRIBUTE_INSIGHT)).thenReturn(true);
        when(assessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.createAttributeInsight(param));
        assertEquals(CREATE_ATTRIBUTE_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND, throwable.getMessage());

        verifyNoInteractions(loadAttributeInsightPort, updateAttributeInsightPort);
    }

    @Test
    void createAttributeInsight_AttributeInsightDoesNotExist_ThrowResourceNotFoundException() {
        var attributeId = 1L;
        var content = "content";
        var currentUserId = UUID.randomUUID();
        var param = new CreateAttributeInsightUseCase.Param(UUID.randomUUID(), attributeId, content, currentUserId);
        var assessmentResult = AssessmentResultMother.validResult();

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), currentUserId, CREATE_ATTRIBUTE_INSIGHT)).thenReturn(true);
        when(assessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadAttributeInsightPort.load(assessmentResult.getId(), attributeId)).thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.createAttributeInsight(param));
        assertEquals(CREATE_ATTRIBUTE_INSIGHT_ATTRIBUTE_INSIGHT_NOT_FOUND, throwable.getMessage());

        verifyNoInteractions(updateAttributeInsightPort);
    }

    @Test
    void createAttributeInsight_validParameters_SuccessfulUpdate() {
        var attributeId = 1L;
        var content = "content";
        var currentUserId = UUID.randomUUID();
        var param = new CreateAttributeInsightUseCase.Param(UUID.randomUUID(), attributeId, content, currentUserId);
        var assessmentResult = AssessmentResultMother.validResult();
        var attributeInsight = AttributeInsightMother.simpleAttributeAiInsight();

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), currentUserId, CREATE_ATTRIBUTE_INSIGHT)).thenReturn(true);
        when(assessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadAttributeInsightPort.load(assessmentResult.getId(), attributeId)).thenReturn(Optional.of(attributeInsight));

        assertDoesNotThrow(() -> service.createAttributeInsight(param));
    }
}
