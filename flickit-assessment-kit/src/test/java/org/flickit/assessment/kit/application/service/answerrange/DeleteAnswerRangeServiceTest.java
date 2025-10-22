package org.flickit.assessment.kit.application.service.answerrange;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.KitVersion;
import org.flickit.assessment.kit.application.port.in.answerrange.DeleteAnswerRangeUseCase;
import org.flickit.assessment.kit.application.port.out.answerrange.DeleteAnswerRangePort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.application.port.out.question.DeleteQuestionPort;
import org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother;
import org.flickit.assessment.kit.test.fixture.application.KitVersionMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteAnswerRangeServiceTest {

    @InjectMocks
    private DeleteAnswerRangeService service;

    @Mock
    private LoadKitVersionPort loadKitVersionPort;

    @Mock
    private LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    @Mock
    private DeleteAnswerRangePort deleteAnswerRangePort;

    @Mock
    private DeleteQuestionPort deleteQuestionPort;

    private final DeleteAnswerRangeService.Param param = createParam(DeleteAnswerRangeUseCase.Param.ParamBuilder::build);
    private final KitVersion kitVersion = KitVersionMother.createKitVersion(AssessmentKitMother.simpleKit());

    @Test
    void deleteAnswerRange_whenCurrentUserIsNotExpertGroupOwner_thenThrowAccessDeniedException() {
        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(UUID.randomUUID());

        var throwable = assertThrows(AccessDeniedException.class, () -> service.deleteAnswerRange(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(deleteAnswerRangePort);
    }

    @Test
    void deleteAnswerRange_whenParamsAreValid_thenSuccessfulDelete() {
        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(param.getCurrentUserId());
        ArgumentCaptor<DeleteQuestionPort.Param> deletQuestionArgumentCaptor = ArgumentCaptor.forClass(DeleteQuestionPort.Param.class);

        service.deleteAnswerRange(param);
        verify(deleteQuestionPort).deleteQuestionAnswerRange(deletQuestionArgumentCaptor.capture());
        verify(deleteAnswerRangePort).delete(param.getAnswerRangeId(), kitVersion.getId());

        assertEquals(param.getAnswerRangeId(), deletQuestionArgumentCaptor.getValue().answerRangeId());
        assertEquals(param.getKitVersionId(), deletQuestionArgumentCaptor.getValue().kitVersionId());
        assertNotNull(deletQuestionArgumentCaptor.getValue().lastModificationTime());
    }

    private DeleteAnswerRangeUseCase.Param createParam(Consumer<DeleteAnswerRangeUseCase.Param.ParamBuilder> changer) {
        var param = paramBuilder();
        changer.accept(param);
        return param.build();
    }

    private DeleteAnswerRangeUseCase.Param.ParamBuilder paramBuilder() {
        return DeleteAnswerRangeUseCase.Param.builder()
            .answerRangeId(2L)
            .kitVersionId(1L)
            .currentUserId(UUID.randomUUID());
    }
}
