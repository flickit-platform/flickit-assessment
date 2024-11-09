package org.flickit.assessment.kit.application.service.answerrange;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.KitVersion;
import org.flickit.assessment.kit.application.port.in.answerrange.CreateAnswerRangeUseCase;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateAnswerRangeServiceTest {

    @InjectMocks
    private CreateAnswerRangeService service;

    @Mock
    private LoadKitVersionPort loadKitVersionPort;

    @Mock
    private LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    @Mock
    private CreateAnswerRangePort createAnswerRangePort;

    private final UUID ownerId = UUID.randomUUID();
    private final KitVersion kitVersion = createKitVersion(simpleKit());

    @Test
    void testCreateAnswerRange_WhenCurrentUserIsNotExpertGroupOwner_ThenThrowAccessDeniedException() {
        var param = createParam(CreateAnswerRangeUseCase.Param.ParamBuilder::build);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(ownerId);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.createAnswerRange(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(createAnswerRangePort);
    }

    @Test
    void testCreateAnswerRange_ValidParams_ThenSuccessfullyCreateAnswerRange() {
        var param = createParam(b -> b.currentUserId(ownerId));

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(ownerId);

        service.createAnswerRange(param);

        ArgumentCaptor<CreateAnswerRangePort.Param> createPortCaptor = ArgumentCaptor.forClass(CreateAnswerRangePort.Param.class);
        verify(createAnswerRangePort, times(1)).persist(createPortCaptor.capture());
        var createPortParam = createPortCaptor.getValue();

        assertEquals(param.getKitVersionId(), createPortParam.kitVersionId());
        assertEquals(param.getTitle(), createPortParam.title());
        assertEquals(param.getCurrentUserId(), createPortParam.createdBy());
        assertEquals(Boolean.TRUE, createPortParam.reusable());
    }

    private CreateAnswerRangeUseCase.Param createParam(Consumer<CreateAnswerRangeUseCase.Param.ParamBuilder> changer) {
        var param = paramBuilder();
        changer.accept(param);
        return param.build();
    }

    private CreateAnswerRangeUseCase.Param.ParamBuilder paramBuilder() {
        return CreateAnswerRangeUseCase.Param.builder()
            .kitVersionId(1L)
            .title("title")
            .currentUserId(UUID.randomUUID());
    }

}
