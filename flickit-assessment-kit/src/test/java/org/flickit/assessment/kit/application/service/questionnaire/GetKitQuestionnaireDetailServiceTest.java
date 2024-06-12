package org.flickit.assessment.kit.application.service.questionnaire;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.domain.ExpertGroup;
import org.flickit.assessment.kit.application.domain.Question;
import org.flickit.assessment.kit.application.port.in.questionnaire.GetKitQuestionnaireDetailUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadActiveKitVersionIdPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadKitExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CheckExpertGroupAccessPort;
import org.flickit.assessment.kit.application.port.out.questionnaire.LoadKitQuestionnaireDetailPort;
import org.flickit.assessment.kit.test.fixture.application.ExpertGroupMother;
import org.flickit.assessment.kit.test.fixture.application.QuestionMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.kit.common.ErrorMessageKey.QUESTIONNAIRE_ID_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetKitQuestionnaireDetailServiceTest {

    @InjectMocks
    private GetKitQuestionnaireDetailService service;

    @Mock
    private LoadKitQuestionnaireDetailPort loadKitQuestionnaireDetailPort;

    @Mock
    private CheckExpertGroupAccessPort checkExpertGroupAccessPort;

    @Mock
    private LoadKitExpertGroupPort loadKitExpertGroupPort;

    @Mock
    private LoadActiveKitVersionIdPort loadActiveKitVersionIdPort;

    @Test
    void testGetKitQuestionnaireDetail_ValidInput_ValidResult() {
        long kitId = 1L;
        long kitVersionId = 3L;
        long questionnaireId = 2L;
        ExpertGroup expertGroup = ExpertGroupMother.createExpertGroup();
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

        when(loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId())).thenReturn(expertGroup);
        when(loadActiveKitVersionIdPort.loadKitVersionId(kitId)).thenReturn(kitVersionId);
        when(checkExpertGroupAccessPort.checkIsMember(expertGroup.getId(), currentUserId)).thenReturn(true);
        when(loadKitQuestionnaireDetailPort.loadKitQuestionnaireDetail(questionnaireId, kitVersionId)).thenReturn(expectedResult);

        GetKitQuestionnaireDetailUseCase.Result actualResult = service.getKitQuestionnaireDetail(param);

        assertNotNull(actualResult);
        assertEquals(expectedResult.description(), actualResult.description());
        assertEquals(expectedResult.questionsCount(), actualResult.questionsCount());
        assertEquals(expectedResult.questions(), actualResult.questions());
        assertEquals(expectedResult.relatedSubjects(), actualResult.relatedSubjects());
    }

    @Test
    void testGetKitQuestionnaireDetail_CurrentUserIsNotMemberOfExpertGroup_ThrowsException() {
        long kitId = 1L;
        long questionnaireId = 2L;
        ExpertGroup expertGroup = ExpertGroupMother.createExpertGroup();
        UUID currentUserId = UUID.randomUUID();

        GetKitQuestionnaireDetailUseCase.Param param = new GetKitQuestionnaireDetailUseCase.Param(kitId,
            questionnaireId,
            currentUserId);

        when(loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId())).thenReturn(expertGroup);
        when(checkExpertGroupAccessPort.checkIsMember(expertGroup.getId(), currentUserId)).thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> service.getKitQuestionnaireDetail(param));
    }

    @Test
    void testGetKitQuestionnaireDetail_QuestionnaireWithGivenIdAndKitIdDoesNotExist_ThrowsException() {
        long kitId = 1L;
        long kitVersionId = 3L;
        long questionnaireId = 2L;
        ExpertGroup expertGroup = ExpertGroupMother.createExpertGroup();
        UUID currentUserId = UUID.randomUUID();

        GetKitQuestionnaireDetailUseCase.Param param = new GetKitQuestionnaireDetailUseCase.Param(kitId,
            questionnaireId,
            currentUserId);

        when(loadKitExpertGroupPort.loadKitExpertGroup(param.getKitId())).thenReturn(expertGroup);
        when(checkExpertGroupAccessPort.checkIsMember(expertGroup.getId(), currentUserId)).thenReturn(true);
        when(loadActiveKitVersionIdPort.loadKitVersionId(kitId)).thenReturn(kitVersionId);
        when(loadKitQuestionnaireDetailPort.loadKitQuestionnaireDetail(questionnaireId, kitVersionId))
            .thenThrow(new ResourceNotFoundException(QUESTIONNAIRE_ID_NOT_FOUND));

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.getKitQuestionnaireDetail(param));
        assertEquals(QUESTIONNAIRE_ID_NOT_FOUND,throwable.getMessage());
    }
}
