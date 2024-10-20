package org.flickit.assessment.kit.application.service.question;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.KitVersion;
import org.flickit.assessment.kit.application.port.in.question.UpdateQuestionsOrderUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.application.port.out.question.UpdateQuestionPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
    private UpdateQuestionPort updateQuestionPort;

    private final UUID ownerId = UUID.randomUUID();
    private final KitVersion kitVersion = createKitVersion(simpleKit());

    @Test
    void testUpdateQuestionsOrder_WhenCurrentUserIsNotExpertGroupOwner_ThenThrowAccessDeniedException() {
        var param = createParam(UpdateQuestionsOrderUseCase.Param.ParamBuilder::build);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(ownerId);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.updateQuestionsOrder(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(updateQuestionPort);
    }

    @Test
    void testUpdateQuestionsOrder_WhenCurrentUserIsExpertGroupOwner_ThenUpdateQuestionsOrder() {
        var param = createParam(b -> b.currentUserId(ownerId));

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(ownerId);
        doNothing().when(updateQuestionPort).updateOrders(any(UpdateQuestionPort.UpdateOrderParam.class));

        service.updateQuestionsOrder(param);

        var outPortParam = ArgumentCaptor.forClass(UpdateQuestionPort.UpdateOrderParam.class);
        verify(updateQuestionPort).updateOrders(outPortParam.capture());
        assertNotNull(outPortParam.getValue());
        assertEquals(param.getKitVersionId(), outPortParam.getValue().kitVersionId());
        assertEquals(param.getQuestionnaireId(), outPortParam.getValue().questionnaireId());
        assertEquals(param.getCurrentUserId(), outPortParam.getValue().lastModifiedBy());
        for (int i = 0; i < param.getOrders().size(); i++) {
            var paramOrder = param.getOrders().get(i);
            var outPortParamOrder = outPortParam.getValue().orders().get(i);
            assertEquals(paramOrder.getIndex(), outPortParamOrder.index());
            assertEquals(paramOrder.getQuestionId(), outPortParamOrder.questionId());
            assertNotNull(outPortParamOrder.code());
        }
        assertNotNull(outPortParam.getValue().lastModificationTime());
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
            .questionnaireId(1L)
            .currentUserId(UUID.randomUUID());
    }
}
