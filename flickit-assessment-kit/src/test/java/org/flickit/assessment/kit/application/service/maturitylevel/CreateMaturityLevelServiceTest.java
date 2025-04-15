package org.flickit.assessment.kit.application.service.maturitylevel;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.KitVersion;
import org.flickit.assessment.kit.application.domain.MaturityLevel;
import org.flickit.assessment.kit.application.port.in.maturitylevel.CreateMaturityLevelUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.application.port.out.maturitylevel.CreateMaturityLevelPort;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateMaturityLevelServiceTest {

    @InjectMocks
    private CreateMaturityLevelService service;

    @Mock
    private CreateMaturityLevelPort createMaturityLevelPort;

    @Mock
    private LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    @Mock
    private LoadKitVersionPort loadKitVersionPort;

    private final UUID ownerId = UUID.randomUUID();
    private final KitVersion kitVersion = createKitVersion(simpleKit());

    @Test
    void testCreateMaturityLevel_WhenCurrentUserIsNotOwner_ShouldThrowAccessDeniedException() {
        var param = createParam(CreateMaturityLevelUseCase.Param.ParamBuilder::build);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(ownerId);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.createMaturityLevel(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());
    }

    @Test
    void testCreateMaturityLevel_WhenCurrentUserIsOwner_ThenCreateMaturityLevel() {
        long levelId = 123L;
        var param = createParam(b -> b.currentUserId(ownerId));

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(ownerId);
        when(createMaturityLevelPort.persist(any(MaturityLevel.class), anyLong(), any(UUID.class))).thenReturn(levelId);

        long actualLevelId = service.createMaturityLevel(param);

        var maturityLevelArgument = ArgumentCaptor.forClass(MaturityLevel.class);
        verify(createMaturityLevelPort, times(1))
            .persist(maturityLevelArgument.capture(), eq(param.getKitVersionId()), eq(param.getCurrentUserId()));

        assertNull(maturityLevelArgument.getValue().getId());
        assertEquals(param.getIndex(), maturityLevelArgument.getValue().getIndex());
        assertEquals(param.getTitle(), maturityLevelArgument.getValue().getTitle());
        assertEquals(param.getDescription(), maturityLevelArgument.getValue().getDescription());
        assertEquals(param.getValue(), maturityLevelArgument.getValue().getValue());
        assertEquals(param.getTranslations(), maturityLevelArgument.getValue().getTranslations());

        assertEquals(levelId, actualLevelId);
    }

    private CreateMaturityLevelUseCase.Param createParam(Consumer<CreateMaturityLevelUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private CreateMaturityLevelUseCase.Param.ParamBuilder paramBuilder() {
        return CreateMaturityLevelUseCase.Param.builder()
            .kitVersionId(1L)
            .index(1)
            .title("title")
            .description("description")
            .value(1)
            .currentUserId(UUID.randomUUID());
    }
}
