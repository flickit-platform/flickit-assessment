package org.flickit.assessment.core.application.service.measure;

import org.flickit.assessment.core.application.port.in.measure.GetAttributeMeasureQuestionsUseCase;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GetAttributeMeasureQuestionsServiceTest {

    private GetAttributeMeasureQuestionsUseCase.Param createParam(Consumer<GetAttributeMeasureQuestionsUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private GetAttributeMeasureQuestionsUseCase.Param.ParamBuilder paramBuilder() {
        return GetAttributeMeasureQuestionsUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .attributeId(1L)
            .measureId(3L)
            .currentUserId(UUID.randomUUID());
    }
}
