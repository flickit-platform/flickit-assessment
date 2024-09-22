package org.flickit.assessment.kit.application.service.questionnaire;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.port.in.questionnaire.UpdateQuestionnaireUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadAssessmentKitPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.questionnaire.UpdateQuestionnairePort;
import org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateQuestionnaireServiceTest {

    @InjectMocks
    private UpdateQuestionnaireService service;

    @Mock
    private UpdateQuestionnairePort updateQuestionnairePort;

    @Mock
    private LoadAssessmentKitPort loadAssessmentKitPort;

    @Mock
    private LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    @Test
    void testUpdateQuestionnaire_WhenCurrentUserIsNotOwner_ThenThrowAccessDeniedException() {
        AssessmentKit kit = AssessmentKitMother.simpleKit();
        long questionnaireId = 2L;
        int index = 1;
        String title = "title";
        String description = "description";
        UUID currentUserId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();

        UpdateQuestionnaireUseCase.Param param =
            new UpdateQuestionnaireUseCase.Param(kit.getId(), questionnaireId, index, title, description, currentUserId);

        when(loadAssessmentKitPort.load(kit.getId())).thenReturn(kit);
        when(loadExpertGroupOwnerPort.loadOwnerId(kit.getExpertGroupId())).thenReturn(ownerId);

        assertThrows(AccessDeniedException.class, () -> service.updateQuestionnaire(param));
    }

    @Test
    void testUpdateQuestionnaire_WhenCurrentUserIsOwner_ThenUpdateQuestionnaire() {
        AssessmentKit kit = AssessmentKitMother.simpleKit();
        long questionnaireId = 2L;
        int index = 1;
        String title = "title";
        String description = "description";
        UUID currentUserId = UUID.randomUUID();

        UpdateQuestionnaireUseCase.Param param =
            new UpdateQuestionnaireUseCase.Param(kit.getId(), questionnaireId, index, title, description, currentUserId);

        when(loadAssessmentKitPort.load(kit.getId())).thenReturn(kit);
        when(loadExpertGroupOwnerPort.loadOwnerId(kit.getExpertGroupId())).thenReturn(currentUserId);

        service.updateQuestionnaire(param);

        verify(updateQuestionnairePort).update(any(UpdateQuestionnairePort.Param.class));
    }
}
