package org.flickit.assessment.kit.application.service.answeroption;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.KitVersion;
import org.flickit.assessment.kit.application.port.in.answeroption.UpdateAnswerOptionUseCase;
import org.flickit.assessment.kit.application.port.out.answeroption.UpdateAnswerOptionPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
class UpdateAnswerOptionServiceTest {

    @InjectMocks
    private UpdateAnswerOptionService service;

    @Mock
    private LoadKitVersionPort loadKitVersionPort;

    @Mock
    private LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    @Mock
    private UpdateAnswerOptionPort updateAnswerOptionPort;


    private final UUID ownerId = UUID.randomUUID();
    private final KitVersion kitVersion = createKitVersion(simpleKit());

    @Test
    void testUpdateAnswerOption_WhenCurrentUserIsNotExpertGroupOwner_ThenThrowAccessDeniedException() {
        UpdateAnswerOptionUseCase.Param param = createParam(UpdateAnswerOptionUseCase.Param.ParamBuilder::build);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(ownerId);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.updateAnswerOption(param));

        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());
        verifyNoInteractions(updateAnswerOptionPort);
    }

    @Test
    void testUpdateAnswerOption_WhenCurrentUserIsExpertGroupOwner_ThenUpdateAnswerOption() {
        UpdateAnswerOptionUseCase.Param param = createParam(b -> b.currentUserId(ownerId));

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(ownerId);
        doNothing().when(updateAnswerOptionPort).updateAnswerOption(any(UpdateAnswerOptionPort.Param.class));

        assertDoesNotThrow(() -> service.updateAnswerOption(param));

        verify(updateAnswerOptionPort).updateAnswerOption(any(UpdateAnswerOptionPort.Param.class));
    }

    private UpdateAnswerOptionUseCase.Param createParam(Consumer<UpdateAnswerOptionUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private UpdateAnswerOptionUseCase.Param.ParamBuilder paramBuilder() {
        return UpdateAnswerOptionUseCase.Param.builder()
            .kitVersionId(1L)
            .answerOptionId(1L)
            .index(1)
            .title("answerOptionTitle")
            .value(1d)
            .currentUserId(UUID.randomUUID());
    }

}
