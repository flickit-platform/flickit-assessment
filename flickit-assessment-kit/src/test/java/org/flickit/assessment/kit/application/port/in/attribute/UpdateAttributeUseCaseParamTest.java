package org.flickit.assessment.kit.application.port.in.attribute;

import jakarta.validation.ConstraintViolationException;
import org.apache.commons.lang3.RandomStringUtils;
import org.flickit.assessment.kit.application.port.in.attribute.UpdateAttributeUseCase.Param;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UpdateAttributeUseCaseParamTest {

    @Test
    void testUpdateAttributeParam_VersionIdIsNull_ErrorMessage() {
        var attributeId = 25L;
        var title = "title";
        var description = "description";
        var subjectId = 18L;
        var index = 2;
        var weight = 1;
        var currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new Param(attributeId, null, index, title, description, weight, subjectId, currentUserId));
        assertThat(throwable).hasMessage("kitVersionId: " + UPDATE_ATTRIBUTE_KIT_VERSION_ID_NOT_NULL);
    }

    @Test
    void testUpdateAttributeParam_attributeIdIsNull_ErrorMessage() {
        var kitVersionId = 16L;
        var title = "title";
        var description = "description";
        var subjectId = 18L;
        var index = 2;
        var weight = 1;
        var currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new Param(null, kitVersionId, index, title, description, weight, subjectId, currentUserId));
        assertThat(throwable).hasMessage("attributeId: " + UPDATE_ATTRIBUTE_ATTRIBUTE_ID_NOT_NULL);
    }

    @Test
    void testUpdateAttributeParam_titleIsBlank_ErrorMessage() {
        var kitVersionId = 16L;
        var attributeId = 25L;
        var title = "     ";
        var description = "description";
        var subjectId = 18L;
        var index = 2;
        var weight = 1;
        var currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new Param(attributeId, kitVersionId, index, title, description, weight, subjectId, currentUserId));
        assertThat(throwable).hasMessage("title: " + UPDATE_ATTRIBUTE_TITLE_NOT_BLANK);
    }

    @Test
    void testUpdateAttributeParam_titleSizeIsLessThanMin_ErrorMessage() {
        var kitVersionId = 16L;
        var attributeId = 25L;
        var title = RandomStringUtils.randomAlphanumeric(2);
        var description = "description";
        var subjectId = 18L;
        var index = 2;
        var weight = 1;
        var currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new Param(attributeId, kitVersionId, index, title, description, weight, subjectId, currentUserId));
        assertThat(throwable).hasMessage("title: " + UPDATE_ATTRIBUTE_TITLE_SIZE_MIN);
    }

    @Test
    void testUpdateAttributeParam_titleSizeIsMoreThanMax_ErrorMessage() {
        var kitVersionId = 16L;
        var attributeId = 25L;
        var title = RandomStringUtils.randomAlphanumeric(101);
        var description = "description";
        var subjectId = 18L;
        var index = 2;
        var weight = 1;
        var currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new Param(attributeId, kitVersionId, index, title, description, weight, subjectId, currentUserId));
        assertThat(throwable).hasMessage("title: " + UPDATE_ATTRIBUTE_TITLE_SIZE_MAX);
    }

    @Test
    void testUpdateAttributeParam_descriptionIsBlank_ErrorMessage() {
        var kitVersionId = 16L;
        var attributeId = 25L;
        var title = "title";
        var description = "     ";
        var subjectId = 18L;
        var index = 2;
        var weight = 1;
        var currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new Param(attributeId, kitVersionId, index, title, description, weight, subjectId, currentUserId));
        assertThat(throwable).hasMessage("description: " + UPDATE_ATTRIBUTE_DESCRIPTION_NOT_BLANK);
    }

    @Test
    void testUpdateAttributeParam_descriptionSizeIsLessThanMin_ErrorMessage() {
        var kitVersionId = 16L;
        var attributeId = 25L;
        var title = "title";
        var description = RandomStringUtils.randomAlphanumeric(2);
        var subjectId = 18L;
        var index = 2;
        var weight = 1;
        var currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new Param(attributeId, kitVersionId, index, title, description, weight, subjectId, currentUserId));
        assertThat(throwable).hasMessage("description: " + UPDATE_ATTRIBUTE_DESCRIPTION_SIZE_MIN);
    }

    @Test
    void testUpdateAttributeParam_descriptionSizeIsMoreThanMax_ErrorMessage() {
        var kitVersionId = 16L;
        var attributeId = 25L;
        var title = "title";
        var description = RandomStringUtils.randomAlphanumeric(501);
        var subjectId = 18L;
        var index = 2;
        var weight = 1;
        var currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new Param(attributeId, kitVersionId, index, title, description, weight, subjectId, currentUserId));
        assertThat(throwable).hasMessage("description: " + UPDATE_ATTRIBUTE_DESCRIPTION_SIZE_MAX);
    }

    @Test
    void testUpdateAttributeParam_subjectIdIsNull_ErrorMessage() {
        var kitVersionId = 16L;
        var attributeId = 25L;
        var title = "title";
        var description = "description";
        var index = 2;
        var weight = 1;
        var currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new Param(attributeId, kitVersionId, index, title, description, weight, null, currentUserId));
        assertThat(throwable).hasMessage("subjectId: " + UPDATE_ATTRIBUTE_SUBJECT_ID_NOT_NULL);
    }

    @Test
    void testUpdateAttributeParam_indexIsNull_ErrorMessage() {
        var kitVersionId = 16L;
        var attributeId = 25L;
        var title = "title";
        var description = "description";
        var subjectId = 18L;
        var weight = 1;
        var currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new Param(attributeId, kitVersionId, null, title, description, weight, subjectId, currentUserId));
        assertThat(throwable).hasMessage("index: " + UPDATE_ATTRIBUTE_INDEX_NOT_NULL);
    }

    @Test
    void testUpdateAttributeParam_WeightIsNull_ErrorMessage() {
        var kitVersionId = 16L;
        var attributeId = 25L;
        var title = "title";
        var description = RandomStringUtils.randomAlphanumeric(10);
        var subjectId = 18L;
        var index = 2;
        var currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new Param(attributeId, kitVersionId, index, title, description, null, subjectId, currentUserId));
        assertThat(throwable).hasMessage("weight: " + UPDATE_ATTRIBUTE_WEIGHT_NOT_NULL);
    }

    @Test
    void testUpdateAttributeParam_WeightIsLessThanMin_ErrorMessage() {
        var kitVersionId = 16L;
        var attributeId = 25L;
        var title = "title";
        var description = RandomStringUtils.randomAlphanumeric(10);
        var subjectId = 18L;
        var index = 2;
        var weight = 0;
        var currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new Param(attributeId, kitVersionId, index, title, description, weight, subjectId, currentUserId));
        assertThat(throwable).hasMessage("weight: " + UPDATE_ATTRIBUTE_WEIGHT_MIN);
    }

    @Test
    void testUpdateAttributeParam_currentUserIdIsNull_ErrorMessage() {
        var kitVersionId = 16L;
        var attributeId = 25L;
        var title = "title";
        var description = "description";
        var subjectId = 18L;
        var index = 2;
        var weight = 1;
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new Param(attributeId, kitVersionId, index, title, description, weight, subjectId, null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }
}