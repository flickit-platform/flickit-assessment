package org.flickit.assessment.kit.application.service.question;

import org.flickit.assessment.common.application.domain.kit.translation.QuestionTranslation;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.kit.application.domain.KitVersion;
import org.flickit.assessment.kit.application.domain.Question;
import org.flickit.assessment.kit.application.port.in.question.UpdateQuestionUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.CountKitAssessmentsPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.application.port.out.question.LoadQuestionPort;
import org.flickit.assessment.kit.application.port.out.question.UpdateQuestionPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.UPDATE_QUESTION_ANSWER_RANGE_ID_NOT_UPDATABLE;
import static org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother.simpleKit;
import static org.flickit.assessment.kit.test.fixture.application.KitVersionMother.createKitVersion;
import static org.flickit.assessment.kit.test.fixture.application.QuestionMother.createQuestion;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateQuestionServiceTest {

    @InjectMocks
    private UpdateQuestionService service;

    @Mock
    private UpdateQuestionPort updateQuestionPort;

    @Mock
    private LoadKitVersionPort loadKitVersionPort;

    @Mock
    private LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    @Mock
    private LoadQuestionPort loadQuestionPort;

    @Mock
    private CountKitAssessmentsPort countKitAssessmentsPort;

    @Captor
    private ArgumentCaptor<UpdateQuestionPort.Param> outPortParam;

    private final UUID ownerId = UUID.randomUUID();
    private final KitVersion kitVersion = createKitVersion(simpleKit());
    private UpdateQuestionUseCase.Param param = createParam(UpdateQuestionUseCase.Param.ParamBuilder::build);

    @Test
    void testUpdateQuestion_whenCurrentUserIsNotExpertGroupOwner_thenThrowAccessDeniedException() {
        param = createParam(b -> b.currentUserId(UUID.randomUUID()));

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(ownerId);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.updateQuestion(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(loadQuestionPort, updateQuestionPort, countKitAssessmentsPort);
    }

    @Test
    void testUpdateQuestion_whenAnswerRangeIdUpdatedAndKitIsUsedInAssessments_thenThrowValidationException() {
        Question question = createQuestion(param.getAnswerRangeId() + 1);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(ownerId);
        when(loadQuestionPort.load(param.getQuestionId(), param.getKitVersionId())).thenReturn(question);
        when(countKitAssessmentsPort.count(kitVersion.getKit().getId())).thenReturn(1L);

        var throwable = assertThrows(ValidationException.class, () -> service.updateQuestion(param));
        assertEquals(UPDATE_QUESTION_ANSWER_RANGE_ID_NOT_UPDATABLE, throwable.getMessageKey());

        verifyNoInteractions(updateQuestionPort);
    }

    @Test
    void testUpdateQuestion_whenCurrentUserIsOwner_thenUpdateQuestion() {
        param = createParam(b -> b.measureId(12L));
        Question question = createQuestion(param.getAnswerRangeId());

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(ownerId);
        when(loadQuestionPort.load(param.getQuestionId(), param.getKitVersionId())).thenReturn(question);

        service.updateQuestion(param);

        verify(updateQuestionPort).update(outPortParam.capture());
        assertNotNull(outPortParam.getValue());
        assertEquals(param.getQuestionId(), outPortParam.getValue().id());
        assertEquals(param.getKitVersionId(), outPortParam.getValue().kitVersionId());
        assertEquals(param.getIndex(), outPortParam.getValue().index());
        assertEquals(param.getTitle(), outPortParam.getValue().title());
        assertEquals(param.getHint(), outPortParam.getValue().hint());
        assertEquals(param.getMayNotBeApplicable(), outPortParam.getValue().mayNotBeApplicable());
        assertEquals(param.getAdvisable(), outPortParam.getValue().advisable());
        assertEquals(param.getAnswerRangeId(), outPortParam.getValue().answerRangeId());
        assertEquals(param.getMeasureId(), outPortParam.getValue().measureId());
        assertEquals(param.getTranslations(), outPortParam.getValue().translations());
        assertEquals(param.getCurrentUserId(), outPortParam.getValue().lastModifiedBy());
        assertNotNull(outPortParam.getValue().lastModificationTime());

        verifyNoInteractions(countKitAssessmentsPort);
    }

    @Test
    void testUpdateQuestion_whenAnswerRangeIdUpdatedAndKitIsNotUsedInAssessments_thenUpdateQuestion() {
        Question question = createQuestion(param.getAnswerRangeId() + 1);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(ownerId);
        when(loadQuestionPort.load(param.getQuestionId(), param.getKitVersionId())).thenReturn(question);
        when(countKitAssessmentsPort.count(kitVersion.getKit().getId())).thenReturn(0L);

        service.updateQuestion(param);

        verify(updateQuestionPort).update(outPortParam.capture());
        assertNotNull(outPortParam.getValue());
        assertEquals(param.getQuestionId(), outPortParam.getValue().id());
        assertEquals(param.getKitVersionId(), outPortParam.getValue().kitVersionId());
        assertEquals(param.getIndex(), outPortParam.getValue().index());
        assertEquals(param.getTitle(), outPortParam.getValue().title());
        assertEquals(param.getHint(), outPortParam.getValue().hint());
        assertEquals(param.getMayNotBeApplicable(), outPortParam.getValue().mayNotBeApplicable());
        assertEquals(param.getAdvisable(), outPortParam.getValue().advisable());
        assertEquals(param.getAnswerRangeId(), outPortParam.getValue().answerRangeId());
        assertEquals(param.getMeasureId(), outPortParam.getValue().measureId());
        assertEquals(param.getTranslations(), outPortParam.getValue().translations());
        assertEquals(param.getCurrentUserId(), outPortParam.getValue().lastModifiedBy());
        assertNotNull(outPortParam.getValue().lastModificationTime());
    }

    @Test
    void testUpdateQuestion_whenAnswerRangeIdOfQuestionIsNUll_thenUpdateQuestion() {
        Question question = createQuestion(null);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(ownerId);
        when(loadQuestionPort.load(param.getQuestionId(), param.getKitVersionId())).thenReturn(question);

        service.updateQuestion(param);

        verify(updateQuestionPort).update(outPortParam.capture());
        assertNotNull(outPortParam.getValue());
        assertEquals(param.getQuestionId(), outPortParam.getValue().id());
        assertEquals(param.getKitVersionId(), outPortParam.getValue().kitVersionId());
        assertEquals(param.getIndex(), outPortParam.getValue().index());
        assertEquals(param.getTitle(), outPortParam.getValue().title());
        assertEquals(param.getHint(), outPortParam.getValue().hint());
        assertEquals(param.getMayNotBeApplicable(), outPortParam.getValue().mayNotBeApplicable());
        assertEquals(param.getAdvisable(), outPortParam.getValue().advisable());
        assertEquals(param.getAnswerRangeId(), outPortParam.getValue().answerRangeId());
        assertEquals(param.getMeasureId(), outPortParam.getValue().measureId());
        assertEquals(param.getTranslations(), outPortParam.getValue().translations());
        assertEquals(param.getCurrentUserId(), outPortParam.getValue().lastModifiedBy());
        assertNotNull(outPortParam.getValue().lastModificationTime());
    }

    private UpdateQuestionUseCase.Param createParam(Consumer<UpdateQuestionUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private UpdateQuestionUseCase.Param.ParamBuilder paramBuilder() {
        return UpdateQuestionUseCase.Param.builder()
            .kitVersionId(1L)
            .questionId(1L)
            .index(1)
            .title("abc")
            .hint("new hint")
            .mayNotBeApplicable(true)
            .answerRangeId(15L)
            .measureId(15L)
            .advisable(false)
            .translations(Map.of("EN", new QuestionTranslation("title", "desc")))
            .currentUserId(ownerId);
    }
}
