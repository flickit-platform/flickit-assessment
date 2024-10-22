package org.flickit.assessment.core.application.port.in.assessment;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.MIGRATE_ASSESSMENT_RESULT_KIT_VERSION_KIT_VERSION_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.MIGRATE_ASSESSMENT_RESULT_KIT_VERSION_ASSESSMENT_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.*;

class MigrateAssessmentResultKitVersionUseCaseParamTest {

    @Test
    void testMigrateAssessmentResultKitVersionUseCase_assessmentIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.assessmentId(null)));
        assertThat(throwable).hasMessage("assessmentId: " + MIGRATE_ASSESSMENT_RESULT_KIT_VERSION_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void testMigrateAssessmentResultKitVersionUseCase_kitVersionIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.kitVersionId(null)));
        assertThat(throwable).hasMessage("kitVersionId: " + MIGRATE_ASSESSMENT_RESULT_KIT_VERSION_KIT_VERSION_ID_NOT_NULL);
    }

    @Test
    void testMigrateAssessmentResultKitVersionUseCase_currentUserIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<MigrateAssessmentResultKitVersionUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private MigrateAssessmentResultKitVersionUseCase.Param.ParamBuilder paramBuilder() {
        return MigrateAssessmentResultKitVersionUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .kitVersionId(1L)
            .currentUserId(UUID.randomUUID());
    }
}
