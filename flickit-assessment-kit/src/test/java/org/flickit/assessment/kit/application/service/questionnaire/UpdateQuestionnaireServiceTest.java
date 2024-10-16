package org.flickit.assessment.kit.application.service.questionnaire;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.KitVersion;
import org.flickit.assessment.kit.application.port.in.questionnaire.UpdateQuestionnaireUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.application.port.out.questionnaire.UpdateQuestionnairePort;
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
import static org.flickit.assessment.kit.test.fixture.application.KitVersionMother.createActiveKitVersion;
import static org.flickit.assessment.kit.test.fixture.application.KitVersionMother.createKitVersion;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateQuestionnaireServiceTest {

    @InjectMocks
    private UpdateQuestionnaireService service;

    @Mock
    private UpdateQuestionnairePort updateQuestionnairePort;

    @Mock
    private LoadKitVersionPort loadKitVersionPort;

    @Mock
    private LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    private final UUID ownerId = UUID.randomUUID();

    private final KitVersion kitVersion = createKitVersion(simpleKit());

    @Test
    void testUpdateQuestionnaire_WhenCurrentUserIsNotExpertGroupOwner_ThenThrowAccessDeniedException() {
        UpdateQuestionnaireUseCase.Param param = createParam(UpdateQuestionnaireUseCase.Param.ParamBuilder::build);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(ownerId);

        AccessDeniedException throwable = assertThrows(AccessDeniedException.class, () -> service.updateQuestionnaire(param));

        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());
        verifyNoInteractions(updateQuestionnairePort);
    }

    @Test
    void testUpdateQuestionnaire_WhenCurrentUserIsExpertGroupOwner_ThenUpdateQuestionnaire() {
        UpdateQuestionnaireUseCase.Param param = createParam(b -> b.currentUserId(ownerId));
        KitVersion kitVersion = createActiveKitVersion(simpleKit());

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(ownerId);
        doNothing().when(updateQuestionnairePort).update(any(UpdateQuestionnairePort.Param.class));

        service.updateQuestionnaire(param);

        var outPortParam = ArgumentCaptor.forClass(UpdateQuestionnairePort.Param.class);
        verify(updateQuestionnairePort).update(outPortParam.capture());
        assertNotNull(outPortParam.getValue());
        assertEquals(param.getQuestionnaireId(), outPortParam.getValue().id());
        assertEquals(param.getKitVersionId(), outPortParam.getValue().kitVersionId());
        assertEquals(param.getTitle(), outPortParam.getValue().title());
        assertEquals(param.getIndex(), outPortParam.getValue().index());
        assertEquals(param.getDescription(), outPortParam.getValue().description());
        assertEquals(param.getCurrentUserId(), outPortParam.getValue().lastModifiedBy());
        assertNotNull(outPortParam.getValue().lastModificationTime());
    }

    private UpdateQuestionnaireUseCase.Param createParam(Consumer<UpdateQuestionnaireUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private UpdateQuestionnaireUseCase.Param.ParamBuilder paramBuilder() {
        return UpdateQuestionnaireUseCase.Param.builder()
            .kitVersionId(1L)
            .questionnaireId(1L)
            .title("abc")
            .index(1)
            .description("description")
            .currentUserId(UUID.randomUUID());
    }
}
