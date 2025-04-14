package org.flickit.assessment.kit.application.service.measure;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.KitVersion;
import org.flickit.assessment.kit.application.domain.Measure;
import org.flickit.assessment.kit.application.port.in.measure.CreateMeasureUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.application.port.out.measure.CreateMeasurePort;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateMeasureServiceTest {

    @InjectMocks
    private CreateMeasureService createMeasureService;

    @Mock
    private CreateMeasurePort createMeasurePort;

    @Mock
    private LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    @Mock
    private LoadKitVersionPort loadKitVersionPort;

    private final UUID ownerId = UUID.randomUUID();
    private final KitVersion kitVersion = createKitVersion(simpleKit());

    @Test
    void testCreateMeasure_whenCurrentUserIsNotExpertGroupOwner_thenThrowAccessDeniedException() {
        var param = createParam(CreateMeasureUseCase.Param.ParamBuilder::build);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(ownerId);

        var throwable = assertThrows(AccessDeniedException.class, () -> createMeasureService.createMeasure(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(createMeasurePort);
    }

    @Test
    void testCreateMeasure_whenCurrentUserIsExpertGroupOwner_thenCreateMeasure() {
        long measureId = 123;
        var param = createParam(b -> b.currentUserId(ownerId));

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(ownerId);
        ArgumentCaptor<Measure> measureCaptor = ArgumentCaptor.forClass(Measure.class);
        when(createMeasurePort.persist(measureCaptor.capture(), eq(param.getKitVersionId()), eq(param.getCurrentUserId())))
            .thenReturn(measureId);

        var result = createMeasureService.createMeasure(param);
        verify(createMeasurePort).persist(measureCaptor.capture(), eq(param.getKitVersionId()), eq(param.getCurrentUserId()));

        assertEquals(measureId, result.id());
        assertEquals(param.getTitle(), measureCaptor.getValue().getTitle());
        assertEquals(param.getDescription(), measureCaptor.getValue().getDescription());
        assertEquals(param.getIndex(), measureCaptor.getValue().getIndex());
        assertNotNull(measureCaptor.getValue().getCreationTime());
        assertNotNull(measureCaptor.getValue().getLastModificationTime());
    }

    private CreateMeasureUseCase.Param createParam(Consumer<CreateMeasureUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private CreateMeasureUseCase.Param.ParamBuilder paramBuilder() {
        return CreateMeasureUseCase.Param.builder()
            .kitVersionId(1L)
            .index(1)
            .title("title")
            .description("description")
            .currentUserId(UUID.randomUUID());
    }
}
