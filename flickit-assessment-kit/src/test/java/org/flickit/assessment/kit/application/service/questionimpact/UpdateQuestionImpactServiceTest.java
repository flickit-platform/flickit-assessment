package org.flickit.assessment.kit.application.service.questionimpact;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.KitVersion;
import org.flickit.assessment.kit.application.port.in.questionimpact.UpdateQuestionImpactUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.application.port.out.questionimpact.UpdateQuestionImpactPort;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateQuestionImpactServiceTest {

    @InjectMocks
    private UpdateQuestionImpactService service;

    @Mock
    private LoadKitVersionPort loadKitVersionPort;

    @Mock
    private LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    @Mock
    private UpdateQuestionImpactPort updateQuestionImpactPort;

    private final UUID ownerId = UUID.randomUUID();
    private final KitVersion kitVersion = createKitVersion(simpleKit());

    @Test
    void testUpdateQuestionImpact_WhenCurrentUserIsNotExpertGroupOwner_ThenThrowAccessDeniedException() {
        var param = createParam(UpdateQuestionImpactUseCase.Param.ParamBuilder::build);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(ownerId);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.updateQuestionImpact(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(updateQuestionImpactPort);
    }

    @Test
    void testUpdateQuestionImpact_WhenCurrentUserIsExpertGroupOwner_ThenUpdateQuestionImpact() {
        var param = createParam(b -> b.currentUserId(ownerId));

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(ownerId);
        doNothing().when(updateQuestionImpactPort).update(any(UpdateQuestionImpactPort.Param.class));

        service.updateQuestionImpact(param);

        ArgumentCaptor<UpdateQuestionImpactPort.Param> outPortParamCaptor = ArgumentCaptor.forClass(UpdateQuestionImpactPort.Param.class);
        verify(updateQuestionImpactPort).update(outPortParamCaptor.capture());
        assertEquals(param.getQuestionImpactId(), outPortParamCaptor.getValue().id());
        assertEquals(param.getKitVersionId(), outPortParamCaptor.getValue().kitVersionId());
        assertEquals(param.getAttributeId(), outPortParamCaptor.getValue().attributeId());
        assertEquals(param.getMaturityLevelId(), outPortParamCaptor.getValue().maturityLevelId());
        assertEquals(param.getWeight(), outPortParamCaptor.getValue().weight());
        assertEquals(param.getCurrentUserId(), outPortParamCaptor.getValue().lastModifiedBy());
        assertNotNull(outPortParamCaptor.getValue().lastModificationTime());
    }

    private UpdateQuestionImpactUseCase.Param createParam(Consumer<UpdateQuestionImpactUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private UpdateQuestionImpactUseCase.Param.ParamBuilder paramBuilder() {
        return UpdateQuestionImpactUseCase.Param.builder()
                .kitVersionId(1L)
                .questionImpactId(2L)
                .weight(1)
                .attributeId(1L)
                .maturityLevelId(1L)
                .currentUserId(UUID.randomUUID());
    }
}
