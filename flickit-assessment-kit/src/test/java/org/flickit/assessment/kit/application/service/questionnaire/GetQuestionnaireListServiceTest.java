package org.flickit.assessment.kit.application.service.questionnaire;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.port.in.questionnaire.GetQuestionnaireListUseCase;
import org.flickit.assessment.kit.application.port.in.questionnaire.GetQuestionnaireListUseCase.Param;
import org.flickit.assessment.kit.application.port.out.assessment.CheckUserAssessmentAccessPort;
import org.flickit.assessment.kit.application.port.out.questionnaire.LoadQuestionnairesByAssessmentIdPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetQuestionnaireListServiceTest {

    @InjectMocks
    private GetQuestionnaireListService service;

    @Mock
    private CheckUserAssessmentAccessPort checkUserAssessmentAccessPort;

    @Mock
    private LoadQuestionnairesByAssessmentIdPort loadQuestionnairesByAssessmentIdPort;

    @Test
    void testGetQuestionnaireList_InvalidCurrentUser_ThrowsException() {
        Param param = new Param(
            UUID.randomUUID(),
            UUID.randomUUID(),
            10,
            0
        );
        when(checkUserAssessmentAccessPort.hasAccess(param.getAssessmentId(), param.getCurrentUserId()))
            .thenReturn(false);

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> service.getQuestionnaireList(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, exception.getMessage());
    }

    @Test
    void testGetQuestionnaireList_ValidParams_ReturnListSuccessfully() {
        Param param = new Param(
            UUID.randomUUID(),
            UUID.randomUUID(),
            10,
            0
        );
        when(checkUserAssessmentAccessPort.hasAccess(param.getAssessmentId(), param.getCurrentUserId()))
            .thenReturn(true);

        var portParam = new LoadQuestionnairesByAssessmentIdPort.Param(param.getAssessmentId(), param.getSize(), param.getPage());
        GetQuestionnaireListUseCase.Subject subject = new GetQuestionnaireListUseCase.Subject(1, "subject");
        GetQuestionnaireListUseCase.QuestionnaireListItem questionnaire = new GetQuestionnaireListUseCase.QuestionnaireListItem(
            0,
            "questionnaire",
            1,
            1,
            0,
            0,
            List.of(
                subject
            )
        );
        var expectedResult = new PaginatedResponse<>(
            List.of(
                questionnaire),
            0,
            10,
            "index",
            "asc",
            1
        );

        when(loadQuestionnairesByAssessmentIdPort.loadAllByAssessmentId(portParam))
            .thenReturn(expectedResult);

        var result = service.getQuestionnaireList(param);

        assertEquals(expectedResult, result);
    }
}
