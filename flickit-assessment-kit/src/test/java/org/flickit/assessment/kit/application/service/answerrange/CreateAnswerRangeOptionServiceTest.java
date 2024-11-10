package org.flickit.assessment.kit.application.service.answerrange;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.kit.application.domain.AnswerRange;
import org.flickit.assessment.kit.application.domain.KitVersion;
import org.flickit.assessment.kit.application.port.in.answerrange.CreateAnswerRangeOptionUseCase;
import org.flickit.assessment.kit.application.port.out.answeroption.CreateAnswerOptionPort;
import org.flickit.assessment.kit.application.port.out.answerrange.LoadAnswerRangePort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
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
import static org.flickit.assessment.kit.common.ErrorMessageKey.CREATE_ANSWER_RANGE_OPTION_ANSWER_RANGE_NON_REUSABLE;
import static org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother.simpleKit;
import static org.flickit.assessment.kit.test.fixture.application.KitVersionMother.createKitVersion;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateAnswerRangeOptionServiceTest {

    private final UUID ownerId = UUID.randomUUID();
    private final KitVersion kitVersion = createKitVersion(simpleKit());

    @InjectMocks
    private CreateAnswerRangeOptionService service;

    @Mock
    private LoadKitVersionPort loadKitVersionPort;

    @Mock
    private LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    @Mock
    private LoadAnswerRangePort loadAnswerRangePort;

    @Mock
    private CreateAnswerOptionPort createAnswerOptionPort;

    @Test
    void testCreateAnswerRangeOption_WhenCurrentUserIsNotOwner_ThrowAccessDeniedException() {
        var param = createParam(CreateAnswerRangeOptionUseCase.Param.ParamBuilder::build);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(ownerId);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.createAnswerRangeOption(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());
    }

    @Test
    void testCreateAnswerRangeOption_WhenAnswerRangeIsNonReusable_ThrowsValidationException() {
        var param = createParam(b -> b.currentUserId(ownerId));
        AnswerRange answerRange = AnswerRangeMother.createNonreusableAnswerRangeWithTwoOptions();

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(ownerId);
        when(loadAnswerRangePort.load(param.getAnswerRangeId(), param.getKitVersionId())).thenReturn(answerRange);

        var throwable = assertThrows(ValidationException.class, () -> service.createAnswerRangeOption(param));
        assertEquals(CREATE_ANSWER_RANGE_OPTION_ANSWER_RANGE_NON_REUSABLE, throwable.getMessageKey());

        verifyNoInteractions(createAnswerOptionPort);
    }

    @Test
    void testCreateAnswerRangeOption_WhenAnswerRangeIsReusable_CreateAnswerOption() {
        long answerOptionId = 123L;
        var param = createParam(b -> b.currentUserId(ownerId));
        AnswerRange answerRange = AnswerRangeMother.createAnswerRangeWithTwoOptions();

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(ownerId);
        when(loadAnswerRangePort.load(param.getAnswerRangeId(), param.getKitVersionId())).thenReturn(answerRange);
        when(createAnswerOptionPort.persist(any())).thenReturn(answerOptionId);

        CreateAnswerRangeOptionUseCase.Result result = service.createAnswerRangeOption(param);
        assertEquals(answerOptionId, result.id());

        var createPortParam = ArgumentCaptor.forClass(CreateAnswerOptionPort.Param.class);
        verify(createAnswerOptionPort, times(1)).persist(createPortParam.capture());
        assertEquals(param.getKitVersionId(), createPortParam.getValue().kitVersionId());
        assertEquals(param.getIndex(), createPortParam.getValue().index());
        assertEquals(param.getTitle(), createPortParam.getValue().title());
        assertEquals(param.getAnswerRangeId(), createPortParam.getValue().answerRangeId());
        assertEquals(param.getValue(), createPortParam.getValue().value());
        assertEquals(param.getCurrentUserId(), createPortParam.getValue().createdBy());
    }

    private CreateAnswerRangeOptionUseCase.Param createParam(Consumer<CreateAnswerRangeOptionUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private CreateAnswerRangeOptionUseCase.Param.ParamBuilder paramBuilder() {
        return CreateAnswerRangeOptionUseCase.Param.builder()
            .kitVersionId(1L)
            .answerRangeId(5163L)
            .index(3)
            .title("first")
            .value(0.5D)
            .currentUserId(UUID.randomUUID());
    }
}
