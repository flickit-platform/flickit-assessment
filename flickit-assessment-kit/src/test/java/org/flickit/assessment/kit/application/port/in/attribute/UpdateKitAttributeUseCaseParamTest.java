package org.flickit.assessment.kit.application.port.in.attribute;

import jakarta.validation.ConstraintViolationException;
import org.apache.commons.lang3.RandomStringUtils;
import org.flickit.assessment.kit.application.port.in.attribute.UpdateKitAttributeUseCase.Param;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UpdateKitAttributeUseCaseParamTest {

    @Test
    void testUpdateKitAttributeParam_kitVersionIdIsNull_ErrorMessage() {
        var attributeId = 25L;
        var code = "code";
        var title = "title";
        var description = "description";
        var subjectId = 18L;
        var index = 2;
        var weight = 1;
        var currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new Param(null, attributeId, code, title, description, subjectId, index, weight, currentUserId));
        assertThat(throwable).hasMessage("kitVersionId: " + UPDATE_KIT_ATTRIBUTE_KIT_VERSION_ID_NOT_NULL);
    }

    @Test
    void testUpdateKitAttributeParam_attributeIdIsNull_ErrorMessage() {
        var kitVersionId = 16L;
        var code = "code";
        var title = "title";
        var description = "description";
        var subjectId = 18L;
        var index = 2;
        var weight = 1;
        var currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new Param(kitVersionId, null, code, title, description, subjectId, index, weight, currentUserId));
        assertThat(throwable).hasMessage("attributeId: " + UPDATE_KIT_ATTRIBUTE_ATTRIBUTE_ID_NOT_NULL);
    }

    @Test
    void testUpdateKitAttributeParam_codeIsBlank_ErrorMessage() {
        var kitVersionId = 16L;
        var attributeId = 25L;
        var code = "     ";
        var title = "title";
        var description = "description";
        var subjectId = 18L;
        var index = 2;
        var weight = 1;
        var currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateKitAttributeUseCase.Param(kitVersionId, attributeId, code, title, description, subjectId, index, weight, currentUserId));
        assertThat(throwable).hasMessage("code: " + UPDATE_KIT_ATTRIBUTE_CODE_NOT_BLANK);
    }

    @Test
    void testUpdateKitAttributeParam_codeSizeIsLessThanMin_ErrorMessage() {
        var kitVersionId = 16L;
        var attributeId = 25L;
        var code = RandomStringUtils.randomAlphanumeric(2);
        var title = "title";
        var description = "description";
        var subjectId = 18L;
        var index = 2;
        var weight = 1;
        var currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new Param(kitVersionId, attributeId, code, title, description, subjectId, index, weight, currentUserId));
        assertThat(throwable).hasMessage("code: " + UPDATE_KIT_ATTRIBUTE_CODE_SIZE_MIN);
    }

    @Test
    void testUpdateKitAttributeParam_codeSizeIsMoreThanMax_ErrorMessage() {
        var kitVersionId = 16L;
        var attributeId = 25L;
        var code = RandomStringUtils.randomAlphanumeric(51);
        var title = "title";
        var description = "description";
        var subjectId = 18L;
        var index = 2;
        var weight = 1;
        var currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new Param(kitVersionId, attributeId, code, title, description, subjectId, index, weight, currentUserId));
        assertThat(throwable).hasMessage("code: " + UPDATE_KIT_ATTRIBUTE_CODE_SIZE_MAX);
    }

    @Test
    void testUpdateKitAttributeParam_titleIsBlank_ErrorMessage() {
        var kitVersionId = 16L;
        var attributeId = 25L;
        var code = "code";
        var title = "     ";
        var description = "description";
        var subjectId = 18L;
        var index = 2;
        var weight = 1;
        var currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateKitAttributeUseCase.Param(kitVersionId, attributeId, code, title, description, subjectId, index, weight, currentUserId));
        assertThat(throwable).hasMessage("title: " + UPDATE_KIT_ATTRIBUTE_TITLE_NOT_BLANK);
    }

    @Test
    void testUpdateKitAttributeParam_titleSizeIsLessThanMin_ErrorMessage() {
        var kitVersionId = 16L;
        var attributeId = 25L;
        var code = "code";
        var title = RandomStringUtils.randomAlphanumeric(2);
        var description = "description";
        var subjectId = 18L;
        var index = 2;
        var weight = 1;
        var currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new Param(kitVersionId, attributeId, code, title, description, subjectId, index, weight, currentUserId));
        assertThat(throwable).hasMessage("title: " + UPDATE_KIT_ATTRIBUTE_TITLE_SIZE_MIN);
    }

    @Test
    void testUpdateKitAttributeParam_titleSizeIsMoreThanMax_ErrorMessage() {
        var kitVersionId = 16L;
        var attributeId = 25L;
        var code = "code";
        var title = RandomStringUtils.randomAlphanumeric(101);
        var description = "description";
        var subjectId = 18L;
        var index = 2;
        var weight = 1;
        var currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new Param(kitVersionId, attributeId, code, title, description, subjectId, index, weight, currentUserId));
        assertThat(throwable).hasMessage("title: " + UPDATE_KIT_ATTRIBUTE_TITLE_SIZE_MAX);
    }

    @Test
    void testUpdateKitAttributeParam_descriptionIsBlank_ErrorMessage() {
        var kitVersionId = 16L;
        var attributeId = 25L;
        var code = "code";
        var title = "title";
        var description = "     ";
        var subjectId = 18L;
        var index = 2;
        var weight = 1;
        var currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateKitAttributeUseCase.Param(kitVersionId, attributeId, code, title, description, subjectId, index, weight, currentUserId));
        assertThat(throwable).hasMessage("description: " + UPDATE_KIT_ATTRIBUTE_DESCRIPTION_NOT_BLANK);
    }

    @Test
    void testUpdateKitAttributeParam_descriptionSizeIsLessThanMin_ErrorMessage() {
        var kitVersionId = 16L;
        var attributeId = 25L;
        var code = "code";
        var title = "title";
        var description = RandomStringUtils.randomAlphanumeric(2);
        var subjectId = 18L;
        var index = 2;
        var weight = 1;
        var currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new Param(kitVersionId, attributeId, code, title, description, subjectId, index, weight, currentUserId));
        assertThat(throwable).hasMessage("description: " + UPDATE_KIT_ATTRIBUTE_DESCRIPTION_SIZE_MIN);
    }

    @Test
    void testUpdateKitAttributeParam_subjectIdIsNull_ErrorMessage() {
        var kitVersionId = 16L;
        var attributeId = 25L;
        var code = "code";
        var title = "title";
        var description = "description";
        var index = 2;
        var weight = 1;
        var currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateKitAttributeUseCase.Param(kitVersionId, attributeId, code, title, description, null, index, weight, currentUserId));
        assertThat(throwable).hasMessage("subjectId: " + UPDATE_KIT_ATTRIBUTE_SUBJECT_ID_NOT_NULL);
    }

    @Test
    void testUpdateKitAttributeParam_indexIsNull_ErrorMessage() {
        var kitVersionId = 16L;
        var attributeId = 25L;
        var code = "code";
        var title = "title";
        var description = "description";
        var subjectId = 18L;
        var weight = 1;
        var currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateKitAttributeUseCase.Param(kitVersionId, attributeId, code, title, description, subjectId, null, weight, currentUserId));
        assertThat(throwable).hasMessage("index: " + UPDATE_KIT_ATTRIBUTE_INDEX_NOT_NULL);
    }

    @Test
    void testUpdateKitAttributeParam_WeightIsNull_ErrorMessage() {
        var kitVersionId = 16L;
        var attributeId = 25L;
        var code = "code";
        var title = "title";
        var description = RandomStringUtils.randomAlphanumeric(10);
        var subjectId = 18L;
        var index = 2;
        var currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new Param(kitVersionId, attributeId, code, title, description, subjectId, index, null, currentUserId));
        assertThat(throwable).hasMessage("weight: " + UPDATE_KIT_ATTRIBUTE_WEIGHT_NOT_NULL);
    }

    @Test
    void testUpdateKitAttributeParam_WeightIsLessThanMin_ErrorMessage() {
        var kitVersionId = 16L;
        var attributeId = 25L;
        var code = "code";
        var title = "title";
        var description = RandomStringUtils.randomAlphanumeric(10);
        var subjectId = 18L;
        var index = 2;
        var weight = 0;
        var currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new Param(kitVersionId, attributeId, code, title, description, subjectId, index, weight, currentUserId));
        assertThat(throwable).hasMessage("weight: " + UPDATE_KIT_ATTRIBUTE_WEIGHT_MIN);
    }

    @Test
    void testGetKitAttributeDetail_currentUserIdIsNull_ErrorMessage() {
        var kitVersionId = 16L;
        var attributeId = 25L;
        var code = "code";
        var title = "title";
        var description = "description";
        var subjectId = 18L;
        var index = 2;
        var weight = 1;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new Param(kitVersionId, attributeId, code, title, description, subjectId, index, weight, null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

}