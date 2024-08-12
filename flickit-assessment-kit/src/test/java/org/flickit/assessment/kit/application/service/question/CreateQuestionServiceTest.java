package org.flickit.assessment.kit.application.service.question;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.port.in.question.CreateQuestionUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadAssessmentKitPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.question.CreateQuestionPort;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateQuestionServiceTest {

    @InjectMocks
    private CreateQuestionService createQuestionService;

    @Mock
    private CreateQuestionPort createQuestionPort;

    @Mock
    private LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    @Mock
    private LoadAssessmentKitPort loadAssessmentKitPort;

    private int index;
    private String title;
    private String hint;
    private Boolean mayNotBeApplicable;
    private Boolean advisable;
    private UUID currentUserId;
    private UUID ownerId;
    private Long questionnaireId;
    private AssessmentKit kit;

    @BeforeEach
    void setUp() {
        index = 1;
        title = "question title";
        hint = "question hint";
        mayNotBeApplicable = true;
        advisable = true;
        currentUserId = UUID.randomUUID();
        ownerId = UUID.randomUUID();
        questionnaireId = 1L;
        kit = AssessmentKitMother.simpleKit();
    }

    @Test
    void testCreateQuestion__WhenCurrentUserIsNotOwner_ShouldThrowAccessDeniedException() {
        CreateQuestionUseCase.Param param = new CreateQuestionUseCase.Param(kit.getId(),
            index,
            title,
            hint,
            mayNotBeApplicable,
            advisable,
            questionnaireId,
            currentUserId);

        when(loadAssessmentKitPort.load(param.getKitId())).thenReturn(kit);
        when(loadExpertGroupOwnerPort.loadOwnerId(kit.getExpertGroupId())).thenReturn(ownerId);

        assertThrows(AccessDeniedException.class, () -> createQuestionService.createQuestion(param));
    }

    @Test
    void testCreateQuestion_WhenCurrentUserIsOwner_ThenCreateQuestion() {
        long questionId = 1;
        ownerId = currentUserId;
        CreateQuestionUseCase.Param param = new CreateQuestionUseCase.Param(kit.getId(),
            index,
            title,
            hint,
            mayNotBeApplicable,
            advisable,
            questionnaireId,
            currentUserId);

        when(loadAssessmentKitPort.load(param.getKitId())).thenReturn(kit);
        when(loadExpertGroupOwnerPort.loadOwnerId(kit.getExpertGroupId())).thenReturn(ownerId);
        when(createQuestionPort.persist(any(CreateQuestionPort.Param.class))).thenReturn(questionId);

        long actualQuestionId = createQuestionService.createQuestion(param);

        assertEquals(questionId, actualQuestionId);
    }
}
