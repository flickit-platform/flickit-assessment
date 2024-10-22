package org.flickit.assessment.core.application.service.assessment;

import org.flickit.assessment.core.application.port.in.assessment.MigrateAssessmentResultKitVersionUseCase;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.function.Consumer;

@ExtendWith(MockitoExtension.class)
class MigrateAssessmentResultKitVersionServiceTest {

    @InjectMocks
    MigrateAssessmentResultKitVersionService service;

    private MigrateAssessmentResultKitVersionUseCase.Param createParam(Consumer<MigrateAssessmentResultKitVersionUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private MigrateAssessmentResultKitVersionUseCase.Param.ParamBuilder paramBuilder() {
        return MigrateAssessmentResultKitVersionUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .kitVersionId(1L)
            .currentUserId(UUID.randomUUID());
    }
}
