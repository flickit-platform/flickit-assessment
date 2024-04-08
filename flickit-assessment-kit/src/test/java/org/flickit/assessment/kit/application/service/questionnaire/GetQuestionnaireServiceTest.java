package org.flickit.assessment.kit.application.service.questionnaire;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.port.in.questionnaire.GetQuestionnaireUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadKitExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CheckExpertGroupAccessPort;
import org.flickit.assessment.kit.application.port.out.questionnaire.LoadQuestionnairePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetQuestionnaireServiceTest {

    @InjectMocks
    private GetQuestionnaireService getQuestionnaireService;

    @Mock
    private LoadQuestionnairePort loadQuestionnairePort;

    @Mock
    private CheckExpertGroupAccessPort checkExpertGroupAccessPort;

    @Mock
    private LoadKitExpertGroupPort loadKitExpertGroupPort;

    @Test
    void testGetQuestionnaire_ValidInput_ValidResult() {
        Long kitId = 1L;
        Long questionnaireId = 2L;
        long expertGroupId = 3L;
        UUID currentUserId = UUID.randomUUID();

        GetQuestionnaireUseCase.Param param = new GetQuestionnaireUseCase.Param(kitId,
            questionnaireId,
            currentUserId);

        LoadQuestionnairePort.Result result = new LoadQuestionnairePort.Result(5,
            List.of("team"),
            "desc",
            List.of(new LoadQuestionnairePort.Result.Question(1L, "title", 1, Boolean.TRUE)));

        when(loadKitExpertGroupPort.loadKitExpertGroupId(kitId)).thenReturn(expertGroupId);
        when(checkExpertGroupAccessPort.checkIsMember(expertGroupId, currentUserId)).thenReturn(true);
        when(loadQuestionnairePort.loadQuestionnaire(questionnaireId, kitId)).thenReturn(result);

        LoadQuestionnairePort.Result questionnaire = getQuestionnaireService.getQuestionnaire(param);

        assertNotNull(questionnaire);
    }

    @Test
    void testGetQuestionnaire_CurrentUserIsNotMemberOfExpertGroup_ThrowsException() {
        Long kitId = 1L;
        Long questionnaireId = 2L;
        long expertGroupId = 3L;
        UUID currentUserId = UUID.randomUUID();

        GetQuestionnaireUseCase.Param param = new GetQuestionnaireUseCase.Param(kitId,
            questionnaireId,
            currentUserId);

        when(loadKitExpertGroupPort.loadKitExpertGroupId(kitId)).thenReturn(expertGroupId);
        when(checkExpertGroupAccessPort.checkIsMember(expertGroupId, currentUserId)).thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> getQuestionnaireService.getQuestionnaire(param));
    }
}
