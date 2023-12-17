package org.flickit.assessment.common.config.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JwtTranslator {

    public static UserDetail parseJson(String content) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(content, UserDetail.class);
        } catch (JsonProcessingException ex) {
            log.debug("Error in parsing jwt", ex);
            return UserDetail.builder().build();
        }
    }
}
