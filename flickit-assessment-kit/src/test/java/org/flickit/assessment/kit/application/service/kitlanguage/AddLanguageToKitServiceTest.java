package org.flickit.assessment.kit.application.service.kitlanguage;

import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.port.in.kitlanguage.AddLanguageToKitUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadAssessmentKitPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitlanguage.CreateKitLanguagePort;
import org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddLanguageToKitServiceTest {

    @InjectMocks
    private AddLanguageToKitService service;

    @Mock
    private LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    @Mock
    private LoadAssessmentKitPort loadAssessmentKitPort;

    @Mock
    private CreateKitLanguagePort createKitLanguagePort;

    private final UUID ownerId = UUID.randomUUID();

    AddLanguageToKitUseCase.Param param = createParam(AddLanguageToKitUseCase.Param.ParamBuilder::build);
    AssessmentKit assessmentKit = AssessmentKitMother.simpleKit();

    @Test
    void testAddLanguageToKit_whenUserIsNotOwner_thenThrowAccessDeniedException() {
        param = createParam(b -> b.currentUserId(UUID.randomUUID()));

        when(loadAssessmentKitPort.load(param.getKitId())).thenReturn(assessmentKit);
        when(loadExpertGroupOwnerPort.loadOwnerId(assessmentKit.getExpertGroupId())).thenReturn(UUID.randomUUID());

        var throwable = assertThrows(AccessDeniedException.class, () -> service.addLanguageToKit(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());
    }

    @Test
    void testAddLanguageToKit_whenParametersAreValid_thenAddLanguageToKit() {
        when(loadAssessmentKitPort.load(param.getKitId())).thenReturn(assessmentKit);
        when(loadExpertGroupOwnerPort.loadOwnerId(assessmentKit.getExpertGroupId())).thenReturn(ownerId);
        doNothing().when(createKitLanguagePort).persist(param.getKitId(), KitLanguage.valueOf(param.getLang()).getId());

        assertDoesNotThrow(() -> service.addLanguageToKit(param));
    }

    private AddLanguageToKitUseCase.Param createParam(Consumer<AddLanguageToKitUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private AddLanguageToKitUseCase.Param.ParamBuilder paramBuilder() {
        return AddLanguageToKitUseCase.Param.builder()
                .kitId(1L)
                .lang("FA")
                .currentUserId(ownerId);
    }
}
