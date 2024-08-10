package org.flickit.assessment.core.application.service.attribute;

import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.port.in.attribute.UpdateAssessmentAttributeInsightUseCase;
import org.flickit.assessment.core.application.port.out.assessment.GetAssessmentPort;
import org.flickit.assessment.core.application.port.out.attributeinsight.LoadAttributeInsightPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.flickit.assessment.core.common.ErrorMessageKey.ASSESSMENT_ID_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateAssessmentAttributeInsightServiceTest {

    @InjectMocks
    private UpdateAssessmentAttributeInsightService service;

    @Mock
    private GetAssessmentPort getAssessmentPort;

    @Mock
    private LoadAttributeInsightPort loadAttributeInsightPort;

    @Test
    void updateAssessmentAttributeInsight_assessmentIdNotFound_resourceNotFoundError() {
        var assessmentId = UUID.randomUUID();
        var attributeId = 1L;
        var content = "content";
        var currentUserId = UUID.randomUUID();
        var param = new UpdateAssessmentAttributeInsightUseCase.Param(assessmentId, attributeId, content, currentUserId);

        when(getAssessmentPort.getAssessmentById(assessmentId)).thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, ()-> service.updateAttributeInsight(param));

        assertEquals(ASSESSMENT_ID_NOT_FOUND, throwable.getMessage());
        verifyNoInteractions(loadAttributeInsightPort);

    }
}
