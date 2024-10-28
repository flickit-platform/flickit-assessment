package org.flickit.assessment.kit.application.service.answerrange;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.port.in.answerrange.GetAnswerRangeListUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CheckExpertGroupAccessPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother;
import org.flickit.assessment.kit.test.fixture.application.KitVersionMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAnswerRangeListServiceTest {

    @InjectMocks
    GetAnswerRangeListService service;

    @Mock
    LoadKitVersionPort loadKitVersionPort;

    @Mock
    CheckExpertGroupAccessPort checkExpertGroupAccessPort;

    @Test
    void testGetAnswerRangeListService_CurrentUserDoesNotHaveAccess_ThrowsAccessDeniedException() {
        var param = createParam(GetAnswerRangeListUseCase.Param.ParamBuilder::build);
        var kitVersion = KitVersionMother.createKitVersion(AssessmentKitMother.simpleKit());

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(checkExpertGroupAccessPort.checkIsMember(kitVersion.getKit().getExpertGroupId(), param.getCurrentUserId())).thenReturn(false);
        assertThrows(AccessDeniedException.class, () -> service.getAnswerRangeList(param));
    }

    private GetAnswerRangeListUseCase.Param createParam(Consumer<GetAnswerRangeListUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private GetAnswerRangeListUseCase.Param.ParamBuilder paramBuilder() {
        return GetAnswerRangeListUseCase.Param.builder()
            .kitVersionId(1L)
            .currentUserId(UUID.randomUUID());
    }
}
