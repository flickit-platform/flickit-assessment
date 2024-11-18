package org.flickit.assessment.kit.application.port.in.kitcustom;

import jakarta.validation.ConstraintViolationException;
import org.apache.commons.lang3.RandomStringUtils;
import org.flickit.assessment.common.exception.ValidationException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.*;

class CreateKitCustomUseCaseParamTest {

    @Test
    void testCreateKitCustomUseCaseParam_kitIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.kitId(null)));
        assertThat(throwable).hasMessage("kitId: " + CREATE_KIT_CUSTOM_KIT_ID_NOT_NULL);
    }

    @Test
    void testCreateKitCustomUseCaseParam_titleParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.title(null)));
        assertThat(throwable).hasMessage("title: " + CREATE_KIT_CUSTOM_TITLE_NOT_NULL);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.title("ab")));
        assertThat(throwable).hasMessage("title: " + CREATE_KIT_CUSTOM_TITLE_SIZE_MIN);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.title(RandomStringUtils.randomAlphabetic(101))));
        assertThat(throwable).hasMessage("title: " + CREATE_KIT_CUSTOM_TITLE_SIZE_MAX);
    }

    @Test
    void testCreateKitCustomUseCaseParam_customDataParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.customData(null)));
        assertThat(throwable).hasMessage("customData: " + CREATE_KIT_CUSTOM_DATA_NOT_NULL);
    }

    @Test
    void testCreateKitCustomUseCaseParam_customDataSubjectsAttributesParamViolatesConstraints_ErrorMessage() {
        List<CreateKitCustomUseCase.Param.KitCustomData.CustomSubject> subjects = new ArrayList<>();
        List<CreateKitCustomUseCase.Param.KitCustomData.CustomAttribute> attributes = new ArrayList<>();

        var validationException = assertThrows(ValidationException.class,
            () -> new CreateKitCustomUseCase.Param.KitCustomData(subjects, attributes));
        assertEquals(CREATE_KIT_CUSTOM_NOT_ALLOWED, validationException.getMessageKey());
    }

    @Test
    void testCreateKitCustomUseCaseParam_customDataSubjectIdParamViolatesConstraints_ErrorMessage() {
        Long subjectId = null;
        int weight = 1;

        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateKitCustomUseCase.Param.KitCustomData.CustomSubject(subjectId, weight));
        assertThat(throwable).hasMessage("id: " + CREATE_KIT_CUSTOM_SUBJECT_ID_NOT_NULL);
    }

    @Test
    void testCreateKitCustomUseCaseParam_customDataSubjectWeightParamViolatesConstraints_ErrorMessage() {
        long subjectId = 1;
        Integer weight = null;

        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateKitCustomUseCase.Param.KitCustomData.CustomSubject(subjectId, weight));
        assertThat(throwable).hasMessage("weight: " + CREATE_KIT_CUSTOM_SUBJECT_WEIGHT_NOT_NULL);
    }

    @Test
    void testCreateKitCustomUseCaseParam_customDataAttributeIdParamViolatesConstraints_ErrorMessage() {
        Long attributeId = null;
        int weight = 1;

        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateKitCustomUseCase.Param.KitCustomData.CustomAttribute(attributeId, weight));
        assertThat(throwable).hasMessage("id: " + CREATE_KIT_CUSTOM_ATTRIBUTE_ID_NOT_NULL);
    }

    @Test
    void testCreateKitCustomUseCaseParam_customDataAttributeWeightParamViolatesConstraints_ErrorMessage() {
        long attributeId = 1;
        Integer weight = null;

        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateKitCustomUseCase.Param.KitCustomData.CustomAttribute(attributeId, weight));
        assertThat(throwable).hasMessage("weight: " + CREATE_KIT_CUSTOM_ATTRIBUTE_WEIGHT_NOT_NULL);
    }

    @Test
    void testCreateKitCustomUseCaseParam_currentUserIdParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<CreateKitCustomUseCase.Param.ParamBuilder> changer) {
        var param = paramBuilder();
        changer.accept(param);
        param.build();
    }

    private CreateKitCustomUseCase.Param.ParamBuilder paramBuilder() {
        var customSubject = new CreateKitCustomUseCase.Param.KitCustomData.CustomSubject(1L, 1);
        var customData = new CreateKitCustomUseCase.Param.KitCustomData(List.of(customSubject), new ArrayList<>());
        return CreateKitCustomUseCase.Param.builder()
            .kitId(1L)
            .title("title")
            .customData(customData)
            .currentUserId(UUID.randomUUID());
    }
}
