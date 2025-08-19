package org.flickit.assessment.core.application.service.space;

import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.port.in.space.GetAssessmentMoveTargetsUseCase;
import org.flickit.assessment.core.application.port.in.space.GetAssessmentMoveTargetsUseCase.Param;
import org.flickit.assessment.core.application.port.out.space.LoadSpacePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.core.common.ErrorMessageKey.GET_ASSESSMENT_MOVE_TARGETS_SPACE_NOT_FOUND;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAssessmentMoveTargetsServiceTest {

    @InjectMocks
    private GetAssessmentMoveTargetsService service;

    @Mock
    private LoadSpacePort loadSpacePort;

    private final Param param = createParam(GetAssessmentMoveTargetsUseCase.Param.ParamBuilder::build);

    @Test
    void testGetAssessmentMoveTargets_whenSpaceDoesNotExist_thenThrowResourceNotFoundException() {
        when(loadSpacePort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.empty());

        var exception = assertThrows(ResourceNotFoundException.class, () -> service.getSpaceList(param));
        assertEquals(GET_ASSESSMENT_MOVE_TARGETS_SPACE_NOT_FOUND, exception.getMessage());
    }

    private Param createParam(Consumer<Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private GetAssessmentMoveTargetsUseCase.Param.ParamBuilder paramBuilder() {
        return GetAssessmentMoveTargetsUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .currentUserId(UUID.randomUUID());
    }

}
