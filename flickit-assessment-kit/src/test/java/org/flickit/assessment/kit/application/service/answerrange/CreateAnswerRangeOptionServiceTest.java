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
import static org.flickit.assessment.kit.test.fixture.application.AnswerRangeMother.createReusableAnswerRangeWithTwoOptions;
import static org.flickit.assessment.kit.test.fixture.application.AnswerRangeMother.createNonReusableAnswerRangeWithTwoOptions;
import static org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother.simpleKit;
import static org.flickit.assessment.kit.test.fixture.application.KitVersionMother.createKitVersion;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateAnswerRangeOptionServiceTest {

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

    private final UUID ownerId = UUID.randomUUID();
    private final KitVersion kitVersion = createKitVersion(simpleKit());

    @Test
    void testCreateAnswerRangeOption_WhenCurrentUserIsNotOwner_ThrowAccessDeniedException() {
        var param = createParam(CreateAnswerRangeOptionUseCase.Param.ParamBuilder::build);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(ownerId);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.createAnswerRangeOption(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(loadAnswerRangePort, createAnswerOptionPort);
    }

    @Test
    void testCreateAnswerRangeOption_WhenAnswerRangeIsNonReusable_ThrowsValidationException() {
        AnswerRange answerRange = createNonReusableAnswerRangeWithTwoOptions();
        var param = createParam(b -> b.currentUserId(ownerId).answerRangeId(answerRange.getId()));

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
        AnswerRange answerRange = createReusableAnswerRangeWithTwoOptions();
        var param = createParam(b -> b.currentUserId(ownerId).answerRangeId(answerRange.getId()));

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(ownerId);
        when(loadAnswerRangePort.load(param.getAnswerRangeId(), param.getKitVersionId())).thenReturn(answerRange);
        when(createAnswerOptionPort.persist(any())).thenReturn(answerOptionId);

        var result = service.createAnswerRangeOption(param);
        assertEquals(answerOptionId, result.id());

        var createPortCaptor = ArgumentCaptor.forClass(CreateAnswerOptionPort.Param.class);
        verify(createAnswerOptionPort, times(1)).persist(createPortCaptor.capture());
        assertEquals(param.getKitVersionId(), createPortCaptor.getValue().kitVersionId());
        assertEquals(param.getIndex(), createPortCaptor.getValue().index());
        assertEquals(param.getTitle(), createPortCaptor.getValue().title());
        assertEquals(param.getAnswerRangeId(), createPortCaptor.getValue().answerRangeId());
        assertEquals(param.getValue(), createPortCaptor.getValue().value());
        assertEquals(param.getCurrentUserId(), createPortCaptor.getValue().createdBy());
    }

    private CreateAnswerRangeOptionUseCase.Param createParam(Consumer<CreateAnswerRangeOptionUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private CreateAnswerRangeOptionUseCase.Param.ParamBuilder paramBuilder() {
        return CreateAnswerRangeOptionUseCase.Param.builder()
            .kitVersionId(kitVersion.getId())
            .answerRangeId(5163L)
            .index(3)
            .title("first")
            .value(0.5D)
            .currentUserId(UUID.randomUUID());
    }
}
