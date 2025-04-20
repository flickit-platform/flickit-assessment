package org.flickit.assessment.kit.application.service.answerrange;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.kit.application.domain.AnswerRange;
import org.flickit.assessment.kit.application.domain.KitVersion;
import org.flickit.assessment.kit.application.port.in.answerrange.UpdateAnswerRangeUseCase;
import org.flickit.assessment.kit.application.port.out.answerrange.LoadAnswerRangePort;
import org.flickit.assessment.kit.application.port.out.answerrange.UpdateAnswerRangePort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.application.port.out.question.CheckQuestionExistencePort;
import org.flickit.assessment.kit.test.fixture.application.AnswerRangeMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.common.util.GenerateHashCodeUtil.generateCode;
import static org.flickit.assessment.kit.common.ErrorMessageKey.UPDATE_ANSWER_RANGE_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.UPDATE_ANSWER_RANGE_TITLE_NOT_NULL;
import static org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother.simpleKit;
import static org.flickit.assessment.kit.test.fixture.application.KitVersionMother.createKitVersion;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateAnswerRangeServiceTest {

    @InjectMocks
    private UpdateAnswerRangeService service;

    @Mock
    private LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    @Mock
    private LoadKitVersionPort loadKitVersionPort;

    @Mock
    private CheckQuestionExistencePort checkQuestionExistencePort;

    @Mock
    private UpdateAnswerRangePort updateAnswerRangePort;

    @Mock
    private LoadAnswerRangePort loadAnswerRangePort;

    private final UUID ownerId = UUID.randomUUID();
    private final KitVersion kitVersion = createKitVersion(simpleKit());
    private final AnswerRange answerRange = AnswerRangeMother.createReusableAnswerRangeWithNoOptions();

    @Test
    void testUpdateAnswerRange_WhenCurrentUserIsNotExpertGroupOwner_ThenThrowAccessDeniedException() {
        var param = createParam(UpdateAnswerRangeUseCase.Param.ParamBuilder::build);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(ownerId);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.updateAnswerRange(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(checkQuestionExistencePort, updateAnswerRangePort);
    }

    @Test
    void testUpdateAnswerRange_WhenAnswerRangeIsUsedByQuestionsAndReusableToNonReusableUpdate_ThenThrowValidationException() {
        var param = createParam(b -> b.currentUserId(ownerId).reusable(false));

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(ownerId);
        when(loadAnswerRangePort.load(param.getAnswerRangeId(), param.getKitVersionId())).thenReturn(answerRange);
        when(checkQuestionExistencePort.existsByAnswerRange(param.getAnswerRangeId(), param.getKitVersionId())).thenReturn(true);

        var throwable = assertThrows(ValidationException.class, () -> service.updateAnswerRange(param));
        assertEquals(UPDATE_ANSWER_RANGE_NOT_ALLOWED, throwable.getMessageKey());

        verifyNoInteractions(updateAnswerRangePort);
    }

    @Test
    void testUpdateAnswerRange_WhenCurrentUserIsExpertGroupOwnerAndReusableAnswerRangeTitleIsNull_ThenThrowValidationException() {
        var param = createParam(b -> b.currentUserId(ownerId).title(null));

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadAnswerRangePort.load(param.getAnswerRangeId(), param.getKitVersionId())).thenReturn(answerRange);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(ownerId);

        var throwable = assertThrows(ValidationException.class, () -> service.updateAnswerRange(param));
        assertEquals(UPDATE_ANSWER_RANGE_TITLE_NOT_NULL, throwable.getMessageKey());

        verifyNoInteractions(updateAnswerRangePort);
    }

    @Test
    void testUpdateAnswerRange_WhenCurrentUserIsExpertGroupOwnerAndReusableAnswerRangeTitleIsValid_ThenUpdateAnswerRange() {
        var param = createParam(b -> b.currentUserId(ownerId));

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(ownerId);
        when(loadAnswerRangePort.load(param.getAnswerRangeId(), param.getKitVersionId())).thenReturn(answerRange);
        doNothing().when(updateAnswerRangePort).update(any(UpdateAnswerRangePort.Param.class));

        service.updateAnswerRange(param);

        ArgumentCaptor<UpdateAnswerRangePort.Param> captor = ArgumentCaptor.forClass(UpdateAnswerRangePort.Param.class);
        verify(updateAnswerRangePort).update(captor.capture());
        assertNotNull(captor.getValue());
        String code = generateCode(param.getTitle());
        assertEquals(param.getAnswerRangeId(), captor.getValue().answerRangeId());
        assertEquals(param.getKitVersionId(), captor.getValue().kitVersionId());
        assertEquals(param.getTitle(), captor.getValue().title());
        assertEquals(code, captor.getValue().code());
        assertEquals(param.getReusable(), captor.getValue().reusable());
        assertEquals(param.getCurrentUserId(), captor.getValue().lastModifiedBy());
        assertEquals(param.getTranslations(), captor.getValue().translations());
        assertNotNull(captor.getValue().lastModificationTime());
    }

    private UpdateAnswerRangeUseCase.Param createParam(Consumer<UpdateAnswerRangeUseCase.Param.ParamBuilder> changer) {
        var param = paramBuilder();
        changer.accept(param);
        return param.build();
    }

    private UpdateAnswerRangeUseCase.Param.ParamBuilder paramBuilder() {
        return UpdateAnswerRangeUseCase.Param.builder()
            .kitVersionId(1L)
            .answerRangeId(2L)
            .title("title")
            .reusable(true)
            .currentUserId(UUID.randomUUID());
    }
}
