package org.flickit.assessment.kit.application.service.answerrange;

import org.flickit.assessment.kit.application.port.in.answerrange.CreateAnswerRangeUseCase;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.function.Consumer;

@ExtendWith(MockitoExtension.class)
class CreateAnswerRangeServiceTest {

    @InjectMocks
    private CreateAnswerRangeService createAnswerRangeService;

    private CreateAnswerRangeUseCase.Param createParam(Consumer<CreateAnswerRangeUseCase.Param.ParamBuilder> changer) {
        var param = paramBuilder();
        changer.accept(param);
        return param.build();
    }

    private CreateAnswerRangeUseCase.Param.ParamBuilder paramBuilder() {
        return CreateAnswerRangeUseCase.Param.builder()
            .kitVersionId(1L)
            .title("title")
            .currentUserId(UUID.randomUUID());
    }

}
