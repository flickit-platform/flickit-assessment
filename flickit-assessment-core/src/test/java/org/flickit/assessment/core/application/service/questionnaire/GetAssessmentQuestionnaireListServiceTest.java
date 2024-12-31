package org.flickit.assessment.core.application.service.questionnaire;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.domain.QuestionnaireListItem;
import org.flickit.assessment.core.application.port.in.questionnaire.GetAssessmentQuestionnaireListUseCase.Param;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.questionnaire.LoadQuestionnairesByAssessmentIdPort;
import org.flickit.assessment.core.application.port.out.questionnaire.GetQuestionnairesProgressPort;
import org.flickit.assessment.core.test.fixture.application.AssessmentResultMother;
import org.flickit.assessment.core.test.fixture.application.QuestionnaireListItemMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_ASSESSMENT_QUESTIONNAIRE_LIST;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAssessmentQuestionnaireListServiceTest {

    @InjectMocks
    private GetAssessmentQuestionnaireListService service;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private LoadAssessmentResultPort loadAssessmentResultPort;

    @Mock
    private LoadQuestionnairesByAssessmentIdPort loadQuestionnairesByAssessmentIdPort;

    @Mock
    private GetQuestionnairesProgressPort getQuestionnairesProgressPort;

    @Test
    void testGetQuestionnaireList_InvalidCurrentUser_ThrowsException() {
        Param param = new Param(UUID.randomUUID(), 10, 0, UUID.randomUUID());
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_QUESTIONNAIRE_LIST))
            .thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.getAssessmentQuestionnaireList(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());
    }

    @Test
    void testGetQuestionnaireList_whenAssessmentDoesNotHaveIssues_ReturnListSuccessfully() {
        Param param = createParam(Param.ParamBuilder::build);
        var portParam = new LoadQuestionnairesByAssessmentIdPort.Param(param.getAssessmentId(), param.getSize(), param.getPage());
        var assessmentResult = AssessmentResultMother.validResult();
        var questionnaires = List.of(QuestionnaireListItemMother.createWithoutIssues(),
            QuestionnaireListItemMother.createWithoutIssues());
        long[] questionnaireIds = questionnaires.stream().mapToLong(QuestionnaireListItem::id).toArray();
        var expectedProgressPortResult = List.of(new GetQuestionnairesProgressPort.Result(questionnaireIds[0], 0, 1),
            new GetQuestionnairesProgressPort.Result(questionnaireIds[0], 0, 1));
        var expectedResult = new PaginatedResponse<>(
            questionnaires,
            0,
            10,
            "index",
            "asc",
            1);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_QUESTIONNAIRE_LIST))
            .thenReturn(true);
        when(loadQuestionnairesByAssessmentIdPort.loadAllByAssessmentId(portParam))
            .thenReturn(expectedResult);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(getQuestionnairesProgressPort.getQuestionnairesProgress(param.getAssessmentId(), questionnaireIds)).thenReturn(expectedProgressPortResult);

        var result = service.getAssessmentQuestionnaireList(param);
        assertEquals(expectedResult, result);
    }

    @Test
    void testGetQuestionnaireList_whenAssessmentHaveIssues_ReturnListContainsIssues() {
        Param param = createParam(Param.ParamBuilder::build);
        var portParam = new LoadQuestionnairesByAssessmentIdPort.Param(param.getAssessmentId(), param.getSize(), param.getPage());
        QuestionnaireListItem questionnaires = QuestionnaireListItemMother.createWithoutIssues();
        var expectedResult = new PaginatedResponse<>(
            List.of(questionnaires),
            0,
            10,
            "index",
            "asc",
            1);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_QUESTIONNAIRE_LIST))
            .thenReturn(true);
        when(loadQuestionnairesByAssessmentIdPort.loadAllByAssessmentId(portParam))
            .thenReturn(expectedResult);

        var result = service.getAssessmentQuestionnaireList(param);
        assertEquals(expectedResult, result);
    }

    private Param createParam(Consumer<Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private Param.ParamBuilder paramBuilder() {
        return Param.builder()
            .assessmentId(UUID.randomUUID())
            .page(0)
            .size(50)
            .currentUserId(UUID.randomUUID());
    }
}
