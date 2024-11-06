package org.flickit.assessment.kit.application.service.question;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.data.jpa.kit.question.QuestionJpaEntity;
import org.flickit.assessment.kit.application.domain.KitVersion;
import org.flickit.assessment.kit.application.domain.Question;
import org.flickit.assessment.kit.application.port.in.question.GetQuestionnaireQuestionsUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CheckExpertGroupAccessPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.application.port.out.question.LoadQuestionnaireQuestionsPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother.simpleKit;
import static org.flickit.assessment.kit.test.fixture.application.KitVersionMother.createKitVersion;
import static org.flickit.assessment.kit.test.fixture.application.QuestionMother.createQuestion;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetQuestionnaireQuestionsServiceTest {

    @InjectMocks
    private GetQuestionnaireQuestionsService service;

    @Mock
    private CheckExpertGroupAccessPort checkExpertGroupAccessPort;

    @Mock
    private LoadKitVersionPort loadKitVersionPort;

    @Mock
    private LoadQuestionnaireQuestionsPort loadQuestionnaireQuestionsPort;

    private final UUID ownerId = UUID.randomUUID();
    private final KitVersion kitVersion = createKitVersion(simpleKit());

    @Test
    void testGetQuestionnaireQuestions_WhenCurrentUserIsNotExpertGroupOwner_ThenThrowAccessDeniedException() {
        var param = createParam(GetQuestionnaireQuestionsUseCase.Param.ParamBuilder::build);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(checkExpertGroupAccessPort.checkIsMember(kitVersion.getKit().getExpertGroupId(), param.getCurrentUserId()))
            .thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.getQuestionnaireQuestions(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(loadQuestionnaireQuestionsPort);
    }

    @Test
    void testGetQuestionnaireQuestions_WhenCurrentUserIsExpertGroupOwner_ThenGetQuestionnaireQuestions() {
        var param = createParam(b -> b.currentUserId(ownerId));
        Question question1 = createQuestion();
        Question question2 = createQuestion();
        var items = List.of(question1, question2);
        PaginatedResponse<Question> pageResult = new PaginatedResponse<>(
            items,
            param.getPage(),
            param.getSize(),
            QuestionJpaEntity.Fields.INDEX,
            Sort.Direction.ASC.name().toLowerCase(),
            items.size());

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(checkExpertGroupAccessPort.checkIsMember(kitVersion.getKit().getExpertGroupId(), param.getCurrentUserId()))
            .thenReturn(true);
        when(loadQuestionnaireQuestionsPort.loadQuestionnaireQuestions(new LoadQuestionnaireQuestionsPort.Param(param.getQuestionnaireId(),
                param.getKitVersionId(),
                param.getPage(),
                param.getSize())))
            .thenReturn(pageResult);

        var paginatedResponse = service.getQuestionnaireQuestions(param);

        assertNotNull(paginatedResponse);
        assertEquals(pageResult.getItems().size(), paginatedResponse.getItems().size());
        for (int i = 0; i < pageResult.getItems().size(); i++) {
            var expected = pageResult.getItems().get(i);
            var actual = paginatedResponse.getItems().get(i);
            assertEquals(expected.getId(), actual.id());
            assertEquals(expected.getTitle(), actual.title());
            assertEquals(expected.getIndex(), actual.index());
            assertEquals(expected.getHint(), actual.hint());
            assertEquals(expected.getMayNotBeApplicable(), actual.mayNotBeApplicable());
            assertEquals(expected.getAdvisable(), actual.advisable());
        }
    }

    public GetQuestionnaireQuestionsUseCase.Param createParam(Consumer<GetQuestionnaireQuestionsUseCase.Param.ParamBuilder> changer) {
        var param = paramBuilder();
        changer.accept(param);
        return param.build();
    }

    public GetQuestionnaireQuestionsUseCase.Param.ParamBuilder paramBuilder() {
        return GetQuestionnaireQuestionsUseCase.Param.builder()
            .kitVersionId(1L)
            .questionnaireId(2L)
            .currentUserId(UUID.randomUUID())
            .page(0)
            .size(20);
    }
}
