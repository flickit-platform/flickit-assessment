package org.flickit.assessment.kit.application.service.question;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.KitVersion;
import org.flickit.assessment.kit.application.port.in.question.UpdateQuestionUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.application.port.out.question.UpdateQuestionPort;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateQuestionServiceTest {

    @InjectMocks
    private UpdateQuestionService service;

    @Mock
    private UpdateQuestionPort updateQuestionPort;

    @Mock
    private LoadKitVersionPort loadKitVersionPort;

    @Mock
    private LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    private final UUID ownerId = UUID.randomUUID();

    private final KitVersion kitVersion = createKitVersion(simpleKit());

    @Test
    void testUpdateQuestion_WhenCurrentUserIsNotExpertGroupOwner_ThenThrowAccessDeniedException() {
        var param = createParam(UpdateQuestionUseCase.Param.ParamBuilder::build);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(ownerId);

        AccessDeniedException throwable = assertThrows(AccessDeniedException.class, () -> service.updateQuestion(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());
        verifyNoInteractions(updateQuestionPort);
    }

    @Test
    void testUpdateQuestion_WhenCurrentUserIsOwner_ThenUpdateQuestion() {
        var param = createParam(b -> b.currentUserId(ownerId));

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(ownerId);
        doNothing().when(updateQuestionPort).update(any(UpdateQuestionPort.Param.class));

        service.updateQuestion(param);

        ArgumentCaptor<UpdateQuestionPort.Param> outPortParam = ArgumentCaptor.forClass(UpdateQuestionPort.Param.class);
        verify(updateQuestionPort).update(outPortParam.capture());
        assertNotNull(outPortParam.getValue());
        assertEquals(param.getQuestionId(), outPortParam.getValue().id());
        assertEquals(param.getKitVersionId(), outPortParam.getValue().kitVersionId());
        assertEquals(param.getIndex(), outPortParam.getValue().index());
        assertEquals(param.getTitle(), outPortParam.getValue().title());
        assertEquals(param.getHint(), outPortParam.getValue().hint());
        assertEquals(param.getMayNotBeApplicable(), outPortParam.getValue().mayNotBeApplicable());
        assertEquals(param.getAdvisable(), outPortParam.getValue().advisable());
        assertEquals(param.getCurrentUserId(), outPortParam.getValue().lastModifiedBy());
        assertNotNull(outPortParam.getValue().lastModificationTime());
    }

    private UpdateQuestionUseCase.Param createParam(Consumer<UpdateQuestionUseCase.Param.ParamBuilder> changer) {
        var param = paramBuilder();
        changer.accept(param);
        return param.build();
    }

    private UpdateQuestionUseCase.Param.ParamBuilder paramBuilder() {
        return UpdateQuestionUseCase.Param.builder()
            .kitVersionId(1L)
            .questionId(1L)
            .index(1)
            .title("abc")
            .hint("new hint")
            .mayNotBeApplicable(true)
            .advisable(false)
            .currentUserId(UUID.randomUUID());
    }
}
