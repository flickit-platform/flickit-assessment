package org.flickit.assessment.kit.application.service.answerrange;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.KitVersion;
import org.flickit.assessment.kit.application.port.in.answerrange.DeleteAnswerRangeUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother;
import org.flickit.assessment.kit.test.fixture.application.KitVersionMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteAnswerRangeServiceTest {

    @InjectMocks
    private DeleteAnswerRangeService service;

    @Mock
    private LoadKitVersionPort loadKitVersionPort;

    @Mock
    private LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    private final DeleteAnswerRangeService.Param param = createParam(DeleteAnswerRangeUseCase.Param.ParamBuilder::build);
    private final KitVersion kitVersion = KitVersionMother.createKitVersion(AssessmentKitMother.simpleKit());

    @Test
    void deleteAnswerRange_whenCurrentUserIsNotExpertGroupOwner_thenThrowAccessDeniedException() {
        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(UUID.randomUUID());

        var throwable = assertThrows(AccessDeniedException.class, () -> service.deleteAnswerRange(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());
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
