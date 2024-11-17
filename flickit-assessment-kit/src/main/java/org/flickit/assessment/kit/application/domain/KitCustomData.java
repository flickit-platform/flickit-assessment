package org.flickit.assessment.kit.application.domain;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.common.exception.ValidationException;

import java.util.List;

import static org.flickit.assessment.kit.common.ErrorMessageKey.*;

public record KitCustomData(List<Subject> subjects, List<Attribute> attributes, List<Questionnaire> questionnaires) {

    public KitCustomData {
        if (subjects.isEmpty() && attributes.isEmpty() && questionnaires.isEmpty())
            throw new ValidationException(CREATE_KIT_CUSTOM_NOT_ALLOWED);
    }

    @Value
    @EqualsAndHashCode(callSuper = false)
    public static class Subject extends SelfValidating<Subject> {

        @NotNull(message = CREATE_KIT_CUSTOM_SUBJECT_ID_NOT_NULL)
        Long subjectId;

        @NotNull(message = CREATE_KIT_CUSTOM_SUBJECT_WEIGHT_NOT_NULL)
        Integer weight;

        public Subject(Long subjectId, Integer weight) {
            this.subjectId = subjectId;
            this.weight = weight;
            this.validateSelf();
        }
    }

    @Value
    @EqualsAndHashCode(callSuper = false)
    public static class Attribute extends SelfValidating<Attribute> {

        @NotNull(message = CREATE_KIT_CUSTOM_ATTRIBUTE_ID_NOT_NULL)
        Long attributeId;

        @NotNull(message = CREATE_KIT_CUSTOM_ATTRIBUTE_WEIGHT_NOT_NULL)
        Integer weight;

        public Attribute(Long attributeId, Integer weight) {
            this.attributeId = attributeId;
            this.weight = weight;
            this.validateSelf();
        }
    }

    @Value
    @EqualsAndHashCode(callSuper = false)
    public static class Questionnaire extends SelfValidating<Questionnaire> {

        @NotNull(message = CREATE_KIT_CUSTOM_QUESTIONNAIRE_ID_NOT_NULL)
        Long questionnaireId;

        @NotNull(message = CREATE_KIT_CUSTOM_QUESTIONNAIRE_DISABLED_NOT_NULL)
        Boolean disabled;

        public Questionnaire(Long questionnaireId, Boolean disabled) {
            this.questionnaireId = questionnaireId;
            this.disabled = disabled;
            this.validateSelf();
        }
    }
}
