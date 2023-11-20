package org.flickit.assessment.kit.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.exception.InvalidContentException;

import static org.flickit.assessment.kit.common.ErrorMessageKey.TRANSLATE_KIT_DSL_UNABLE_TO_PARSE_JSON;

public class DslTranslator {

    public AssessmentKitDslModel parseJson(String content) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(content, AssessmentKitDslModel.class);
        } catch (JsonProcessingException ex) {
            throw new InvalidContentException(TRANSLATE_KIT_DSL_UNABLE_TO_PARSE_JSON, ex);
        }
    }
}
