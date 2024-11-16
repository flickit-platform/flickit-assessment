package org.flickit.assessment.kit.application.service.kitversion.validatekitversion;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.kit.application.port.in.kitversion.ValidateKitVersionUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother;
import org.flickit.assessment.kit.test.fixture.application.KitVersionMother;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.VALIDATE_KIT_VERSION_STATUS_INVALID;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValidateKitVersionServiceTest {

    @InjectMocks
    private ValidateKitVersionService service;

    @Mock
    LoadKitVersionPort loadKitVersionPort;

    @Mock
    LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    @Test
    void testValidateKitVersionService_WhenInvalidKitVersion_ShouldThrowValidationException() {
        var param = createParam(ValidateKitVersionUseCase.Param.ParamBuilder::build);
        var assessmentKit = AssessmentKitMother.simpleKit();
        var kitVersion = KitVersionMother.createActiveKitVersion(assessmentKit);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);

        var throwable = assertThrows(ValidationException.class, () -> service.validate(param));
        assertEquals(VALIDATE_KIT_VERSION_STATUS_INVALID, throwable.getMessageKey());
    }

    @Test
    void testValidateKitVersionService_WhenCurrentUserIsNotExpertGroupOwner_ShouldThrowAccessDeniedException() {
        var param = createParam(ValidateKitVersionUseCase.Param.ParamBuilder::build);
        var assessmentKit = AssessmentKitMother.simpleKit();
        var kitVersion = KitVersionMother.createKitVersion(assessmentKit);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(assessmentKit.getExpertGroupId())).thenReturn(UUID.randomUUID());

        var throwable = assertThrows(AccessDeniedException.class, () -> service.validate(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());
    }

    private ValidateKitVersionUseCase.Param createParam(Consumer<ValidateKitVersionUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private ValidateKitVersionUseCase.Param.ParamBuilder paramBuilder() {
        return ValidateKitVersionUseCase.Param.builder()
            .kitVersionId(1L)
            .currentUserId(UUID.randomUUID());
    }

}
