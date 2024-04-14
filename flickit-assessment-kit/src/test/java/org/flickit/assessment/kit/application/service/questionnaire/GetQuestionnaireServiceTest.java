package org.flickit.assessment.kit.application.service.questionnaire;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.Question;
import org.flickit.assessment.kit.application.port.in.questionnaire.GetKitQuestionnaireDetailUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadKitExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CheckExpertGroupAccessPort;
import org.flickit.assessment.kit.application.port.out.questionnaire.LoadKitQuestionnaireDetailPort;
import org.flickit.assessment.kit.test.fixture.application.QuestionMother;
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
    private GetKitQuestionnaireDetailService service;

    @Mock
    private LoadKitQuestionnaireDetailPort loadKitQuestionnaireDetailPort;

    @Mock
    private CheckExpertGroupAccessPort checkExpertGroupAccessPort;

    @Mock
    private LoadKitExpertGroupPort loadKitExpertGroupPort;

    @Test
    void testGetQuestionnaire_CurrentUserIsNotMemberOfExpertGroup_ThrowsException() {
        Long kitId = 1L;
        Long questionnaireId = 2L;
        long expertGroupId = 3L;
        UUID currentUserId = UUID.randomUUID();

        GetKitQuestionnaireDetailUseCase.Param param = new GetKitQuestionnaireDetailUseCase.Param(kitId,
            questionnaireId,
            currentUserId);

        when(loadKitExpertGroupPort.loadKitExpertGroupId(kitId)).thenReturn(expertGroupId);
        when(checkExpertGroupAccessPort.checkIsMember(expertGroupId, currentUserId)).thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> service.getKitQuestionnaireDetail(param));
    }

    @Test
    void testGetQuestionnaire_ValidInput_ValidResult() {
        long kitId = 1L;
        long questionnaireId = 2L;
        long expertGroupId = 3L;
        UUID currentUserId = UUID.randomUUID();

        GetKitQuestionnaireDetailUseCase.Param param = new GetKitQuestionnaireDetailUseCase.Param(kitId,
            questionnaireId,
            currentUserId);

        Question question = QuestionMother.createQuestion("qCode",
            "qTitle",
            1,
            "qHint",
            true,
            true,
            1L);

        var expectedResult = new LoadKitQuestionnaireDetailPort.Result(5,
            List.of("team"),
            "desc",
            List.of(question));

        when(loadKitExpertGroupPort.loadKitExpertGroupId(kitId)).thenReturn(expertGroupId);
        when(checkExpertGroupAccessPort.checkIsMember(expertGroupId, currentUserId)).thenReturn(true);
        when(loadKitQuestionnaireDetailPort.loadKitQuestionnaireDetail(questionnaireId, kitId)).thenReturn(expectedResult);

        GetKitQuestionnaireDetailUseCase.Result actualResult = service.getKitQuestionnaireDetail(param);

        assertNotNull(actualResult);
        assertEquals(expectedResult.description(), actualResult.description());
        assertEquals(expectedResult.questionsCount(), actualResult.questionsCount());
        assertEquals(expectedResult.questions(), actualResult.questions());
        assertEquals(expectedResult.relatedSubjects(), actualResult.relatedSubjects());
    }
}
