package org.flickit.assessment.kit.application.service.question;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.KitVersion;
import org.flickit.assessment.kit.application.port.in.question.UpdateQuestionsOrderUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.application.port.out.question.UpdateQuestionsOrderPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother.simpleKit;
import static org.flickit.assessment.kit.test.fixture.application.KitVersionMother.createKitVersion;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateQuestionsOrderServiceTest {

    @InjectMocks
    private UpdateQuestionsOrderService service;

    @Mock
    private LoadKitVersionPort loadKitVersionPort;

    @Mock
    private LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    @Mock
    private UpdateQuestionsOrderPort updateQuestionsOrderPort;

    private final UUID ownerId = UUID.randomUUID();

    private final KitVersion kitVersion = createKitVersion(simpleKit());

    @Test
    void testUpdateQuestionsOrderService_WhenCurrentUserIsNotExpertGroupOwner_ThenThrowAccessDeniedException() {
        var param = createParam(UpdateQuestionsOrderUseCase.Param.ParamBuilder::build);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(ownerId);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.updateQuestionsOrder(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(updateQuestionsOrderPort);
    }

    @Test
    void testUpdateQuestionsOrderService_WhenCurrentUserIsExpertGroupOwner_ThenUpdateQuestionsOrder() {
        var param = createParam(b -> b.currentUserId(ownerId));

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(ownerId);
        doNothing().when(updateQuestionsOrderPort).updateQuestionsOrder(any(UpdateQuestionsOrderPort.Param.class));

        assertDoesNotThrow(() -> service.updateQuestionsOrder(param));

        verify(updateQuestionsOrderPort).updateQuestionsOrder(any(UpdateQuestionsOrderPort.Param.class));
    }

    private UpdateQuestionsOrderUseCase.Param createParam(Consumer<UpdateQuestionsOrderUseCase.Param.ParamBuilder> changer) {
        var param = paramBuilder();
        changer.accept(param);
        return param.build();
    }

    private UpdateQuestionsOrderUseCase.Param.ParamBuilder paramBuilder() {
        return UpdateQuestionsOrderUseCase.Param.builder()
            .kitVersionId(1L)
            .orders(List.of(new UpdateQuestionsOrderUseCase.Param.QuestionOrder(1L, 1),
                new UpdateQuestionsOrderUseCase.Param.QuestionOrder(2L, 2)))
            .currentUserId(UUID.randomUUID());
    }
}
