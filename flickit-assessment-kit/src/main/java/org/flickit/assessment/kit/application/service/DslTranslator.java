package org.flickit.assessment.kit.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.common.exception.InvalidContentException;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;

import static org.flickit.assessment.kit.common.ErrorMessageKey.TRANSLATE_KIT_DSL_UNABLE_TO_PARSE_JSON;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DslTranslator {

    public static AssessmentKitDslModel parseJson(String content) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(content, AssessmentKitDslModel.class);
        } catch (JsonProcessingException ex) {
            throw new InvalidContentException(TRANSLATE_KIT_DSL_UNABLE_TO_PARSE_JSON, ex);
        }
    }
}
