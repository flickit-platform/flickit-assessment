package org.flickit.assessment.core.application.port.in.assessmentreport;

import jakarta.validation.ConstraintViolationException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CreateAssessmentReportMetadataUseCaseParamTest {

    @Test
    void testCreateAssessmentReportMetadataUseCaseParam_assessmentIdParamViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.assessmentId(null)));
        assertThat(throwable).hasMessage("assessmentId: " + CREATE_ASSESSMENT_REPORT_METADATA_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void testCreateAssessmentReportMetadataUseCaseParam_metadataParamViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.metadata(null)));
        assertThat(throwable).hasMessage("metadata: " + CREATE_ASSESSMENT_REPORT_METADATA_METADATA_NOT_NULL);
    }

    @Test
    void testCreateAssessmentReportMetadataUseCaseParam_currentUserIdParamViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    @Test
    void testCreateAssessmentReportMetadataUseCaseMetadataParam_introParamViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createMetadataParam(b -> b.intro(RandomStringUtils.randomAlphabetic(1001))));
        assertThat(throwable).hasMessage("intro: " + CREATE_ASSESSMENT_REPORT_METADATA_INTRO_SIZE_MAX);
    }

    @Test
    void testCreateAssessmentReportMetadataUseCaseMetadataParam_prosAndConsParamViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createMetadataParam(b -> b.prosAndCons(RandomStringUtils.randomAlphabetic(1001))));
        assertThat(throwable).hasMessage("prosAndCons: " + CREATE_ASSESSMENT_REPORT_METADATA_PROS_AND_CONS_SIZE_MAX);
    }

    private void createParam(Consumer<CreateAssessmentReportMetadataUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private CreateAssessmentReportMetadataUseCase.Param.ParamBuilder paramBuilder() {
        return CreateAssessmentReportMetadataUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .metadata(metadataParamBuilder().build())
            .currentUserId(UUID.randomUUID());
    }

    private void createMetadataParam(Consumer<CreateAssessmentReportMetadataUseCase.MetadataParam.MetadataParamBuilder> changer) {
        var paramBuilder = metadataParamBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private CreateAssessmentReportMetadataUseCase.MetadataParam.MetadataParamBuilder metadataParamBuilder() {
        return CreateAssessmentReportMetadataUseCase.MetadataParam.builder()
            .intro("intro")
            .prosAndCons("prosAndCons")
            .steps("steps")
            .participants("participants");
    }
}