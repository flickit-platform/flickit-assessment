package org.flickit.assessment.kit.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ValidationException;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.springframework.stereotype.Component;

@Component
public class DslTranslator {

    public AssessmentKitDslModel parseJson(String content) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(content, AssessmentKitDslModel.class);
        } catch (JsonProcessingException e) {
            throw new ValidationException(e.getMessage());
        }
    }
}
