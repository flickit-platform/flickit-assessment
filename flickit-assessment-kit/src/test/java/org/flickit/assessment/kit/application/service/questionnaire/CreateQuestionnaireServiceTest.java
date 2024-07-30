package org.flickit.assessment.kit.application.service.questionnaire;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.Questionnaire;
import org.flickit.assessment.kit.application.port.in.questionnaire.CreateQuestionnaireUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadAssessmentKitPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.questionnaire.CreateQuestionnairePort;
import org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateQuestionnaireServiceTest {

    @InjectMocks
    private CreateQuestionnaireService createQuestionnaireService;

    @Mock
    private CreateQuestionnairePort createQuestionnairePort;

    @Mock
    private LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    @Mock
    private LoadAssessmentKitPort loadAssessmentKitPort;

    private int index;
    private String title;
    private String description;
    private UUID currentUserId;
    private UUID ownerId;
    private AssessmentKit kit;

    @BeforeEach
    void setUp() {
        index = 2;
        title = "team";
        description = "about team";
        currentUserId = UUID.randomUUID();
        ownerId = UUID.randomUUID();
        kit = AssessmentKitMother.simpleKit();
    }

    @Test
    void testCreateQuestionnaire_WhenCurrentUserIsNotOwner_ShouldThrowAccessDeniedException() {
        CreateQuestionnaireUseCase.Param param = new CreateQuestionnaireUseCase.Param(kit.getId(),
            index,
            title,
            description,
            currentUserId);

        when(loadAssessmentKitPort.load(param.getKitId())).thenReturn(kit);
        when(loadExpertGroupOwnerPort.loadOwnerId(kit.getExpertGroupId())).thenReturn(ownerId);

        assertThrows(AccessDeniedException.class, () -> createQuestionnaireService.createQuestionnaire(param));
    }

    @Test
    void testCreateQuestionnaire_WhenCurrentUserIsOwner_ThenCreateQuestionnaire() {
        long questionnaireId = 1;
        currentUserId = ownerId;
        CreateQuestionnaireUseCase.Param param = new CreateQuestionnaireUseCase.Param(kit.getId(),
            index,
            title,
            description,
            currentUserId);

        when(loadAssessmentKitPort.load(param.getKitId())).thenReturn(kit);
        when(loadExpertGroupOwnerPort.loadOwnerId(kit.getExpertGroupId())).thenReturn(ownerId);
        when(createQuestionnairePort.persist(any(Questionnaire.class), anyLong(), any(UUID.class))).thenReturn(questionnaireId);

        long actualQuestionnaireId = createQuestionnaireService.createQuestionnaire(param);
        assertEquals(questionnaireId, actualQuestionnaireId);
    }
}
