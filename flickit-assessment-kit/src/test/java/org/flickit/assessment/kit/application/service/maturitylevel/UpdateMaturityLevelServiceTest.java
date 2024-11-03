package org.flickit.assessment.kit.application.service.maturitylevel;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.KitVersion;
import org.flickit.assessment.kit.application.domain.MaturityLevel;
import org.flickit.assessment.kit.application.port.in.maturitylevel.UpdateMaturityLevelUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.application.port.out.maturitylevel.UpdateMaturityLevelPort;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateMaturityLevelServiceTest {

    @InjectMocks
    UpdateMaturityLevelService service;

    @Mock
    LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    @Mock
    LoadKitVersionPort loadKitVersionPort;

    @Mock
    UpdateMaturityLevelPort updateMaturityLevelPort;

    private final UUID ownerId = UUID.randomUUID();
    private final KitVersion kitVersion = createKitVersion(simpleKit());

    @Test
    void testUpdateMaturityLevelService_UserIsNotExpertGroupOwner_AccessDeniedException() {
        var param = createParam(UpdateMaturityLevelUseCase.Param.ParamBuilder::build);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(ownerId);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.updateMaturityLevel(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(updateMaturityLevelPort);
    }

    @Test
    void testUpdateMaturityLevelService_ValidParameters_SuccessfulUpdate() {
        var param = createParam(b -> b.currentUserId(ownerId));

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(ownerId);
        doNothing().when(updateMaturityLevelPort).update(any(), eq(param.getKitVersionId()), notNull(), eq(param.getCurrentUserId()));

        service.updateMaturityLevel(param);

        ArgumentCaptor<MaturityLevel> updatePortParam = ArgumentCaptor.forClass(MaturityLevel.class);
        verify(updateMaturityLevelPort).update(updatePortParam.capture(), eq(param.getKitVersionId()), notNull(), eq(param.getCurrentUserId()));

        assertEquals(param.getMaturityLevelId(), updatePortParam.getValue().getId());
        assertEquals(param.getTitle(), updatePortParam.getValue().getTitle());
        assertEquals(param.getIndex(), updatePortParam.getValue().getIndex());
        assertEquals(param.getValue(), updatePortParam.getValue().getValue());
        assertEquals(param.getDescription(), updatePortParam.getValue().getDescription());
    }

    private UpdateMaturityLevelUseCase.Param createParam(Consumer<UpdateMaturityLevelUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private UpdateMaturityLevelUseCase.Param.ParamBuilder paramBuilder() {
        return UpdateMaturityLevelUseCase.Param.builder()
            .maturityLevelId(1L)
            .kitVersionId(2L)
            .title("title")
            .index(3)
            .description("team description")
            .value(2)
            .currentUserId(UUID.randomUUID());
    }
}
