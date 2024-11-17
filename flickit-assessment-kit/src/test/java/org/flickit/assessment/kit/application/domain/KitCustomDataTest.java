package org.flickit.assessment.kit.application.domain;

import jakarta.validation.ConstraintViolationException;
import org.flickit.assessment.common.exception.ValidationException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.*;

class KitCustomDataTest {

    @Test
    void givenKitCustomDataEmpty_WhenCreatingAKitCustomData_ThenThrowsValidationException() {
        List<KitCustomData.Subject> subjects = new ArrayList<>();
        List<KitCustomData.Attribute> attributes = new ArrayList<>();
        List<KitCustomData.Questionnaire> questionnaires = new ArrayList<>();

        var validationException = assertThrows(ValidationException.class,
            () -> new KitCustomData(subjects, attributes, questionnaires));
        assertEquals(CREATE_KIT_CUSTOM_NOT_ALLOWED, validationException.getMessageKey());
    }

    @Test
    void givenSubjectIdIsNull_WhenCreatingASubjectCustomData_ThenThrowException() {
        Long subjectId = null;
        int weight = 1;

        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new KitCustomData.Subject(subjectId, weight));
        assertThat(throwable).hasMessage("subjectId: " + CREATE_KIT_CUSTOM_SUBJECT_ID_NOT_NULL);
    }

    @Test
    void givenSubjectWeightIsNull_WhenCreatingASubjectCustomData_ThenThrowException() {
        long subjectId = 1;
        Integer weight = null;

        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new KitCustomData.Subject(subjectId, weight));
        assertThat(throwable).hasMessage("weight: " + CREATE_KIT_CUSTOM_SUBJECT_WEIGHT_NOT_NULL);
    }

    @Test
    void givenAttributeIdIsNull_WhenCreatingAnAttributeCustomData_ThenThrowException() {
        Long attributeId = null;
        int weight = 1;

        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new KitCustomData.Attribute(attributeId, weight));
        assertThat(throwable).hasMessage("attributeId: " + CREATE_KIT_CUSTOM_ATTRIBUTE_ID_NOT_NULL);
    }

    @Test
    void givenAttributeWeightIsNull_WhenCreatingAnAttributeCustomData_ThenThrowException() {
        long attributeId = 1;
        Integer weight = null;

        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new KitCustomData.Attribute(attributeId, weight));
        assertThat(throwable).hasMessage("weight: " + CREATE_KIT_CUSTOM_ATTRIBUTE_WEIGHT_NOT_NULL);
    }

    @Test
    void givenQuestionnaireIdIsNull_WhenCreatingAQuestionnaireCustomData_ThenThrowException() {
        Long questionnaireId = null;
        boolean disabled = true;

        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new KitCustomData.Questionnaire(questionnaireId, disabled));
        assertThat(throwable).hasMessage("questionnaireId: " + CREATE_KIT_CUSTOM_QUESTIONNAIRE_ID_NOT_NULL);
    }

    @Test
    void givenQuestionnaireDisabledIsNull_WhenCreatingAQuestionnaireCustomData_ThenThrowException() {
        long questionnaireId = 1;
        Boolean disabled = null;

        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new KitCustomData.Questionnaire(questionnaireId, disabled));
        assertThat(throwable).hasMessage("disabled: " + CREATE_KIT_CUSTOM_QUESTIONNAIRE_DISABLED_NOT_NULL);
    }
}
