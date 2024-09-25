package org.flickit.assessment.kit.application.service.maturitylevel;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.port.in.maturitylevel.DeleteMaturityLevelUseCase;
import org.flickit.assessment.kit.application.port.in.maturitylevel.DeleteMaturityLevelUseCase.Param;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadAssessmentKitPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.maturitylevel.DeleteMaturityLevelPort;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteMaturityLevelServiceTest {

    @InjectMocks
    DeleteMaturityLevelService service;

    @Mock
    LoadAssessmentKitPort loadAssessmentKitPort;

    @Mock
    LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    @Mock
    DeleteMaturityLevelPort deleteMaturityLevelPort;

    @Test
    void testDeleteMaturityLevelService_CurrentUserIsNotExpertGroupOwner_AccessDenied() {
        Param param = createParam(DeleteMaturityLevelUseCase.Param.ParamBuilder::build);
        var assessmentKit = AssessmentKitMother.simpleKit();

        when(loadAssessmentKitPort.load(param.getKitId())).thenReturn(assessmentKit);
        when(loadExpertGroupOwnerPort.loadOwnerId(assessmentKit.getExpertGroupId())).thenReturn(UUID.randomUUID());

        var throwable = assertThrows(AccessDeniedException.class, () -> service.delete(param));

        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());
    }

    @Test
    void testDeleteMaturityLevelService_ValidParameters_ShouldDeleteMaturityLevel() {
        Param param = createParam(DeleteMaturityLevelUseCase.Param.ParamBuilder::build);
        var assessmentKit = AssessmentKitMother.simpleKit();

        when(loadAssessmentKitPort.load(param.getKitId())).thenReturn(assessmentKit);
        when(loadExpertGroupOwnerPort.loadOwnerId(assessmentKit.getExpertGroupId())).thenReturn(param.getCurrentUserId());

        assertDoesNotThrow(() -> service.delete(param));
        verify(deleteMaturityLevelPort).delete(assessmentKit.getId(), assessmentKit.getKitVersionId());
    }

    private DeleteMaturityLevelUseCase.Param createParam(Consumer<DeleteMaturityLevelUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private DeleteMaturityLevelUseCase.Param.ParamBuilder paramBuilder() {
        return DeleteMaturityLevelUseCase.Param.builder()
            .maturityLevelId(1L)
            .kitId(2L)
            .currentUserId(UUID.randomUUID());
    }
}
