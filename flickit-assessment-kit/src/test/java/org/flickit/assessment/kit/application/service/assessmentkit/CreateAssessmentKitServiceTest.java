package org.flickit.assessment.kit.application.service.assessmentkit;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.KitVersionStatus;
import org.flickit.assessment.kit.application.port.in.assessmentkit.CreateAssessmentKitUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.CreateAssessmentKitPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
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
class CreateAssessmentKitServiceTest {

    @InjectMocks
    private CreateAssessmentKitService service;

    @Mock
    private LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    @Mock
    private CreateAssessmentKitPort createAssessmentKitPort;

    private final UUID ownerId = UUID.randomUUID();

    @Test
    void testCreateAssessmentKit_CurrentUserDoesNotHaveAccess_ShouldFailToCreateAssessmentKit() {
        var param = createParam(CreateAssessmentKitUseCase.Param.ParamBuilder::build);

        when(loadExpertGroupOwnerPort.loadOwnerId(param.getExpertGroupId())).thenReturn(ownerId);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.createAssessmentKit(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verify(loadExpertGroupOwnerPort).loadOwnerId(param.getExpertGroupId());
        verifyNoMoreInteractions(createAssessmentKitPort);
    }

    @Test
    void testCreateAssessmentKit_validParameters_CreateAssessmentKit() {
        var kitId = 2L;
        var kitVersionId = 3L;
        var portResult = new CreateAssessmentKitPort.Result(kitId, kitVersionId);
        var param = createParam(b -> b.currentUserId(ownerId));

        when(loadExpertGroupOwnerPort.loadOwnerId(param.getExpertGroupId())).thenReturn(ownerId);
        when(createAssessmentKitPort.persist(any())).thenReturn(portResult);

        var result = service.createAssessmentKit(param);

        assertNotNull(result);
        assertEquals(portResult.kitId(), result.kitId());

        ArgumentCaptor<CreateAssessmentKitPort.Param> paramCaptor = ArgumentCaptor.forClass(CreateAssessmentKitPort.Param.class);
        verify(createAssessmentKitPort).persist(paramCaptor.capture());
        assertEquals(AssessmentKit.generateSlugCode(param.getTitle()), paramCaptor.getValue().code());
        assertEquals(param.getTitle(), paramCaptor.getValue().title());
        assertEquals(param.getSummary(), paramCaptor.getValue().summary());
        assertEquals(param.getAbout(), paramCaptor.getValue().about());
        assertFalse(paramCaptor.getValue().published());
        assertEquals(param.getIsPrivate(), paramCaptor.getValue().isPrivate());
        assertEquals(param.getExpertGroupId(), paramCaptor.getValue().expertGroupId());
        assertEquals(KitVersionStatus.UPDATING, paramCaptor.getValue().kitVersionStatus());
        assertEquals(param.getCurrentUserId(), paramCaptor.getValue().createdBy());
    }

    private CreateAssessmentKitUseCase.Param createParam(Consumer<CreateAssessmentKitUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private CreateAssessmentKitUseCase.Param.ParamBuilder paramBuilder() {
        return CreateAssessmentKitUseCase.Param.builder()
            .title("Enterprise")
            .summary("summary")
            .about("about")
            .isPrivate(true)
            .expertGroupId(123L)
            .currentUserId(UUID.randomUUID());
    }
}
