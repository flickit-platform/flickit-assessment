package org.flickit.assessment.kit.application.service.subject;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.KitVersion;
import org.flickit.assessment.kit.application.port.in.subject.UpdateSubjectUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.application.port.out.subject.UpdateSubjectPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.common.util.GenerateHashCodeUtil.generateCode;
import static org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother.simpleKit;
import static org.flickit.assessment.kit.test.fixture.application.KitVersionMother.createKitVersion;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateSubjectServiceTest {

    @InjectMocks
    private UpdateSubjectService service;

    @Mock
    private UpdateSubjectPort updateSubjectPort;

    @Mock
    private LoadKitVersionPort loadKitVersionPort;

    @Mock
    private LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    private final UUID ownerId = UUID.randomUUID();
    private final KitVersion kitVersion = createKitVersion(simpleKit());

    @Test
    void testUpdateSubject_WhenCurrentUserIsNotExpertGroupOwner_ThenThrowAccessDeniedException() {
        var param = createParam(UpdateSubjectUseCase.Param.ParamBuilder::build);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(ownerId);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.updateSubject(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(updateSubjectPort);
    }

    @Test
    void testUpdateSubject_WhenCurrentUserIsExpertGroupOwner_ThenUpdateSubject() {
        var param = createParam(b -> b.currentUserId(ownerId));

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(ownerId);
        doNothing().when(updateSubjectPort).update(any());

        service.updateSubject(param);

        ArgumentCaptor<UpdateSubjectPort.Param> updateParamCaptor = ArgumentCaptor.forClass(UpdateSubjectPort.Param.class);
        verify(updateSubjectPort).update(updateParamCaptor.capture());
        assertEquals(param.getSubjectId(), updateParamCaptor.getValue().id());
        assertEquals(param.getKitVersionId(), updateParamCaptor.getValue().kitVersionId());
        assertEquals(param.getTitle(), updateParamCaptor.getValue().title());
        assertEquals(generateCode(param.getTitle()), updateParamCaptor.getValue().code());
        assertEquals(param.getIndex(), updateParamCaptor.getValue().index());
        assertEquals(param.getDescription(), updateParamCaptor.getValue().description());
        assertEquals(param.getWeight(), updateParamCaptor.getValue().weight());
        assertEquals(param.getCurrentUserId(), updateParamCaptor.getValue().lastModifiedBy());
        assertNotNull(updateParamCaptor.getValue().lastModificationTime());
    }

    private UpdateSubjectUseCase.Param createParam(Consumer<UpdateSubjectUseCase.Param.ParamBuilder> changer) {
        var param = paramBuilder();
        changer.accept(param);
        return param.build();
    }

    private UpdateSubjectUseCase.Param.ParamBuilder paramBuilder() {
        return UpdateSubjectUseCase.Param.builder()
            .kitVersionId(1L)
            .subjectId(1L)
            .index(1)
            .title("subject title")
            .description("subject description")
            .weight(1)
            .currentUserId(UUID.randomUUID());
    }
}
