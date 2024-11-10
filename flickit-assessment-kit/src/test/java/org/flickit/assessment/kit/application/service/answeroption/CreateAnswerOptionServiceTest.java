package org.flickit.assessment.kit.application.service.answeroption;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.kit.application.domain.AnswerRange;
import org.flickit.assessment.kit.application.domain.KitVersion;
import org.flickit.assessment.kit.application.domain.Question;
import org.flickit.assessment.kit.application.port.in.answeroption.CreateAnswerOptionUseCase.Param;
import org.flickit.assessment.kit.application.port.in.answeroption.CreateAnswerOptionUseCase.Result;
import org.flickit.assessment.kit.application.port.out.answeroption.CreateAnswerOptionPort;
import org.flickit.assessment.kit.application.port.out.answerrange.CreateAnswerRangePort;
import org.flickit.assessment.kit.application.port.out.answerrange.LoadAnswerRangePort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.application.port.out.question.LoadQuestionPort;
import org.flickit.assessment.kit.application.port.out.question.UpdateQuestionPort;
import org.flickit.assessment.kit.test.fixture.application.AnswerRangeMother;
import org.flickit.assessment.kit.test.fixture.application.QuestionMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.CREATE_ANSWER_OPTION_ANSWER_RANGE_REUSABLE;
import static org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother.simpleKit;
import static org.flickit.assessment.kit.test.fixture.application.Constants.QUESTION_CODE1;
import static org.flickit.assessment.kit.test.fixture.application.Constants.QUESTION_TITLE1;
import static org.flickit.assessment.kit.test.fixture.application.KitVersionMother.createKitVersion;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateAnswerOptionServiceTest {

    private final UUID ownerId = UUID.randomUUID();
    private final KitVersion kitVersion = createKitVersion(simpleKit());

    @InjectMocks
    private CreateAnswerOptionService service;

    @Mock
    private LoadKitVersionPort loadKitVersionPort;

    @Mock
    private LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    @Mock
    private LoadQuestionPort loadQuestionPort;

    @Mock
    private CreateAnswerRangePort createAnswerRangePort;

    @Mock
    private UpdateQuestionPort updateQuestionPort;

    @Mock
    private LoadAnswerRangePort loadAnswerRangePort;

    @Mock
    private CreateAnswerOptionPort createAnswerOptionPort;

    @Test
    void testCreateAnswerOption_WhenCurrentUserIsNotOwner_ThrowAccessDeniedException() {
        var param = createParam(Param.ParamBuilder::build);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(ownerId);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.createAnswerOption(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());
    }

    @Test
    void testCreateAnswerOption_WhenAnswerRangeIdIsNonReusable_CreateAnswerOption() {
        long answerOptionId = 123L;
        var param = createParam(b -> b.currentUserId(ownerId));
        Question question = QuestionMother.createQuestionWithImpacts();
        AnswerRange answerRange = AnswerRangeMother.createNonReusableAnswerRangeWithTwoOptions();

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(ownerId);
        when(loadQuestionPort.load(param.getQuestionId(), param.getKitVersionId())).thenReturn(question);
        when(loadAnswerRangePort.load(question.getAnswerRangeId(), param.getKitVersionId())).thenReturn(answerRange);
        when(createAnswerOptionPort.persist(any())).thenReturn(answerOptionId);

        Result result = service.createAnswerOption(param);
        assertEquals(answerOptionId, result.id());

        var createPortParam = ArgumentCaptor.forClass(CreateAnswerOptionPort.Param.class);
        verify(createAnswerOptionPort, times(1)).persist(createPortParam.capture());
        assertEquals(param.getKitVersionId(), createPortParam.getValue().kitVersionId());
        assertEquals(param.getIndex(), createPortParam.getValue().index());
        assertEquals(param.getTitle(), createPortParam.getValue().title());
        assertEquals(question.getAnswerRangeId(), createPortParam.getValue().answerRangeId());
        assertEquals(param.getValue(), createPortParam.getValue().value());
        assertEquals(param.getCurrentUserId(), createPortParam.getValue().createdBy());

        verifyNoInteractions(createAnswerRangePort, updateQuestionPort);
    }

    @Test
    void testCreateAnswerOption_WhenAnswerRangeIdIsNullForQuestion_CreateAnswerRangeAndAnswerOption() {
        long answerOptionId = 123L;
        Long questionAnswerRangeId = null;
        Long expectedAnswerRangeId = 153L;
        var param = createParam(b -> b.currentUserId(ownerId));
        Question question = QuestionMother.createQuestion(QUESTION_CODE1, QUESTION_TITLE1, 1, null, true, true, questionAnswerRangeId, null);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(ownerId);
        when(loadQuestionPort.load(param.getQuestionId(), param.getKitVersionId())).thenReturn(question);
        when(createAnswerRangePort.persist(any())).thenReturn(expectedAnswerRangeId);
        doNothing().when(updateQuestionPort).updateAnswerRange(any());
        when(createAnswerOptionPort.persist(any())).thenReturn(answerOptionId);

        Result result = service.createAnswerOption(param);
        assertEquals(answerOptionId, result.id());

        var createAnswerRangePortParam = ArgumentCaptor.forClass(CreateAnswerRangePort.Param.class);
        verify(createAnswerRangePort, times(1)).persist(createAnswerRangePortParam.capture());
        assertEquals(param.getKitVersionId(), createAnswerRangePortParam.getValue().kitVersionId());
        assertNull(createAnswerRangePortParam.getValue().title());
        assertFalse(createAnswerRangePortParam.getValue().reusable());
        assertEquals(param.getCurrentUserId(), createAnswerRangePortParam.getValue().createdBy());

        var updateQuestionPortParam = ArgumentCaptor.forClass(UpdateQuestionPort.UpdateAnswerRangeParam.class);
        verify(updateQuestionPort, times(1)).updateAnswerRange(updateQuestionPortParam.capture());
        assertEquals(question.getId(), updateQuestionPortParam.getValue().id());
        assertEquals(param.getKitVersionId(), updateQuestionPortParam.getValue().kitVersionId());
        assertEquals(expectedAnswerRangeId, updateQuestionPortParam.getValue().answerRangeId());
        assertNotNull(updateQuestionPortParam.getValue().lastModificationTime());
        assertEquals(param.getCurrentUserId(), updateQuestionPortParam.getValue().lastModifiedBy());

        var createPortParam = ArgumentCaptor.forClass(CreateAnswerOptionPort.Param.class);
        verify(createAnswerOptionPort, times(1)).persist(createPortParam.capture());
        assertEquals(expectedAnswerRangeId, createPortParam.getValue().answerRangeId());
    }

    @Test
    void testCreateAnswerOption_WhenAnswerRangeIsReusable_ThrowsValidationException() {
        var param = createParam(b -> b.currentUserId(ownerId));
        Question question = QuestionMother.createQuestion();
        AnswerRange answerRange = AnswerRangeMother.createAnswerRangeWithFourOptions();

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(ownerId);
        when(loadQuestionPort.load(param.getQuestionId(), param.getKitVersionId())).thenReturn(question);
        when(loadAnswerRangePort.load(question.getAnswerRangeId(), param.getKitVersionId())).thenReturn(answerRange);

        var throwable = assertThrows(ValidationException.class, () -> service.createAnswerOption(param));
        assertEquals(CREATE_ANSWER_OPTION_ANSWER_RANGE_REUSABLE, throwable.getMessageKey());

        verifyNoInteractions(createAnswerRangePort, createAnswerOptionPort);
    }

    private Param createParam(Consumer<Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private Param.ParamBuilder paramBuilder() {
        return Param.builder()
            .kitVersionId(1L)
            .questionId(5163L)
            .index(3)
            .title("first")
            .value(0.5D)
            .currentUserId(UUID.randomUUID());
    }

}
