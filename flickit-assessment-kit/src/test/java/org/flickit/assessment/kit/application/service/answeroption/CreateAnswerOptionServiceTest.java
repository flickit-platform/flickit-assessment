package org.flickit.assessment.kit.application.service.answeroption;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.KitVersion;
import org.flickit.assessment.kit.application.port.in.answeroption.CreateAnswerOptionUseCase.Param;
import org.flickit.assessment.kit.application.port.in.answeroption.CreateAnswerOptionUseCase.Result;
import org.flickit.assessment.kit.application.port.out.answeroption.CreateAnswerOptionPort;
import org.flickit.assessment.kit.application.port.out.answerrange.CreateAnswerRangePort;
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
import static org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother.simpleKit;
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
    private CreateAnswerRangePort createAnswerRangePort;

    @Mock
    private CreateAnswerOptionPort createAnswerOptionPort;

    @Test
    void testCreateAnswerOption_WhenCurrentUserIsNotOwner_ShouldThrowAccessDeniedException() {
        var param = createParam(Param.ParamBuilder::build);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(ownerId);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.createAnswerOption(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());
    }

    @Test
    void testCreateAnswerOption_WhenCurrentUserIsOwner_ThenCreateAnswerOption() {
        long answerOptionId = 123L;
        var param = createParam(b -> b.currentUserId(ownerId));

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(ownerId);
        when(createAnswerOptionPort.persist(any())).thenReturn(answerOptionId);

        Result result = service.createAnswerOption(param);
        assertEquals(answerOptionId, result.id());
        assertEquals(param.getAnswerRangeId(), result.answerRangeId());

        var createPortParam = ArgumentCaptor.forClass(CreateAnswerOptionPort.Param.class);
        verify(createAnswerOptionPort, times(1)).persist(createPortParam.capture());
        assertEquals(param.getKitVersionId(), createPortParam.getValue().kitVersionId());
        assertEquals(param.getIndex(), createPortParam.getValue().index());
        assertEquals(param.getTitle(), createPortParam.getValue().title());
        assertEquals(param.getAnswerRangeId(), createPortParam.getValue().answerRangeId());
        assertEquals(param.getValue(), createPortParam.getValue().value());
        assertEquals(param.getCurrentUserId(), createPortParam.getValue().createdBy());

        verifyNoInteractions(createAnswerRangePort);
    }

    @Test
    void testCreateAnswerOption_WhenAnswerRangeIdIsNull_ThenCreateAnswerRangeAndAnswerOption() {
        long answerOptionId = 123L;
        long answerRangeId = 125L;
        var param = createParam(b ->
            b.currentUserId(ownerId)
                .answerRangeId(null));

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(ownerId);
        when(createAnswerRangePort.persist(any())).thenReturn(answerRangeId);
        when(createAnswerOptionPort.persist(any())).thenReturn(answerOptionId);

        Result result = service.createAnswerOption(param);
        assertEquals(answerOptionId, result.id());
        assertEquals(answerRangeId, result.answerRangeId());

        var createAnswerRangePortParam = ArgumentCaptor.forClass(CreateAnswerRangePort.Param.class);
        verify(createAnswerRangePort, times(1)).persist(createAnswerRangePortParam.capture());
        assertEquals(param.getKitVersionId(), createAnswerRangePortParam.getValue().kitVersionId());
        assertNull(createAnswerRangePortParam.getValue().title());
        assertFalse(createAnswerRangePortParam.getValue().reusable());
        assertEquals(param.getCurrentUserId(), createAnswerRangePortParam.getValue().createdBy());

        var createPortParam = ArgumentCaptor.forClass(CreateAnswerOptionPort.Param.class);
        verify(createAnswerOptionPort, times(1)).persist(createPortParam.capture());
        assertEquals(answerRangeId, createPortParam.getValue().answerRangeId());
    }

    private Param createParam(Consumer<Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private Param.ParamBuilder paramBuilder() {
        return Param.builder()
            .kitVersionId(1L)
            .index(3)
            .title("first")
            .answerRangeId(5163L)
            .value(0.5D)
            .currentUserId(UUID.randomUUID());
    }

}