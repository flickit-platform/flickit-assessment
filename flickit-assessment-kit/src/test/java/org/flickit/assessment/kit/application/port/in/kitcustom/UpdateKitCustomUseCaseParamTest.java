package org.flickit.assessment.kit.application.port.in.kitcustom;

import jakarta.validation.ConstraintViolationException;
import org.apache.commons.lang3.RandomStringUtils;
import org.flickit.assessment.common.exception.ValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.*;

class UpdateKitCustomUseCaseParamTest {

    @Test
    void testUpdateKitCustomUseCaseParam_kitCustomIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.kitCustomId(null)));
        assertThat(throwable).hasMessage("kitCustomId: " + UPDATE_KIT_CUSTOM_KIT_CUSTOM_ID_NOT_NULL);
    }

    @Test
    void testUpdateKitCustomUseCaseParam_kitIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.kitId(null)));
        assertThat(throwable).hasMessage("kitId: " + UPDATE_KIT_CUSTOM_KIT_ID_NOT_NULL);
    }

    @Test
    void testUpdateKitCustomUseCaseParam_titleParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.title(null)));
        assertThat(throwable).hasMessage("title: " + UPDATE_KIT_CUSTOM_TITLE_NOT_NULL);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.title("  ab  ")));
        assertThat(throwable).hasMessage("title: " + UPDATE_KIT_CUSTOM_TITLE_SIZE_MIN);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.title(RandomStringUtils.randomAlphabetic(101))));
        assertThat(throwable).hasMessage("title: " + UPDATE_KIT_CUSTOM_TITLE_SIZE_MAX);
    }

    @Test
    void testUpdateKitCustomUseCaseParam_customDataParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.customData(null)));
        assertThat(throwable).hasMessage("customData: " + UPDATE_KIT_CUSTOM_DATA_NOT_NULL);
    }

    @Test
    void testUpdateKitCustomUseCaseParam_customDataSubjectsAttributesParamViolatesConstraints_ErrorMessage() {
        var validationException = assertThrows(ValidationException.class,
            () -> createParam(b -> b.customData(createCustomDataParam(c ->
                c.customSubjects(List.of()).customAttributes(List.of())))));
        assertEquals(UPDATE_KIT_CUSTOM_EMPTY_CUSTOM_NOT_ALLOWED, validationException.getMessageKey());
    }

    @Test
    void testUpdateKitCustomUseCaseParam_customDataSubjectIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateKitCustomUseCase.Param.KitCustomData.CustomSubject(null, 1));
        assertThat(throwable).hasMessage("id: " + UPDATE_KIT_CUSTOM_SUBJECT_ID_NOT_NULL);
    }

    @Test
    void testUpdateKitCustomUseCaseParam_customDataSubjectWeightParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateKitCustomUseCase.Param.KitCustomData.CustomSubject(123L, null));
        assertThat(throwable).hasMessage("weight: " + UPDATE_KIT_CUSTOM_SUBJECT_WEIGHT_NOT_NULL);
    }

    @Test
    void testUpdateKitCustomUseCaseParam_customDataAttributeIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateKitCustomUseCase.Param.KitCustomData.CustomAttribute(null, 1));
        assertThat(throwable).hasMessage("id: " + UPDATE_KIT_CUSTOM_ATTRIBUTE_ID_NOT_NULL);
    }

    @Test
    void testUpdateKitCustomUseCaseParam_customDataAttributeWeightParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateKitCustomUseCase.Param.KitCustomData.CustomAttribute(123L, null));
        assertThat(throwable).hasMessage("weight: " + UPDATE_KIT_CUSTOM_ATTRIBUTE_WEIGHT_NOT_NULL);
    }

    @Test
    void testUpdateKitCustomUseCaseParam_currentUserIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<UpdateKitCustomUseCase.Param.ParamBuilder> changer) {
        var param = paramBuilder();
        changer.accept(param);
        param.build();
    }

    private UpdateKitCustomUseCase.Param.ParamBuilder paramBuilder() {
        return UpdateKitCustomUseCase.Param.builder()
            .kitCustomId(12L)
            .kitId(1L)
            .title("title")
            .customData(createCustomDataParam(UpdateKitCustomUseCase.Param.KitCustomData.KitCustomDataBuilder::build))
            .currentUserId(UUID.randomUUID());
    }

    private UpdateKitCustomUseCase.Param.KitCustomData createCustomDataParam(Consumer<UpdateKitCustomUseCase.Param.KitCustomData.KitCustomDataBuilder> changer) {
        var param = KitCustomDataBuilder();
        changer.accept(param);
        return param.build();
    }

    private UpdateKitCustomUseCase.Param.KitCustomData.KitCustomDataBuilder KitCustomDataBuilder() {
        var customSubject = new UpdateKitCustomUseCase.Param.KitCustomData.CustomSubject(1L, 1);
        var customAttribute = new UpdateKitCustomUseCase.Param.KitCustomData.CustomAttribute(1L, 1);
        return UpdateKitCustomUseCase.Param.KitCustomData.builder()
            .customSubjects(List.of(customSubject))
            .customAttributes(List.of(customAttribute));
    }
}
