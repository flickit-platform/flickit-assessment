package org.flickit.flickitassessmentcore.adapter.out.persistence.maturitylevel;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.out.LoadMLByKitPort;
import org.flickit.flickitassessmentcore.domain.AnswerOptionImpact;
import org.flickit.flickitassessmentcore.domain.MaturityLevel;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.Set;

@RequiredArgsConstructor
@Component
public class MaturityLevelPersistenceAdapter implements LoadMLByKitPort {
    @Override
    public Set<MaturityLevel> loadMLByKitId(Long kitId) {
        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
            .setConnectTimeout(Duration.ofSeconds(10))
            .setReadTimeout(Duration.ofSeconds(10))
            .messageConverters(new MappingJackson2HttpMessageConverter());
        RestTemplate restTemplate = restTemplateBuilder.build();
        String url = "https://api.example.com/data";
        ResponseEntity<Set<MaturityLevel>> responseEntity = restTemplate.exchange(
            url,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<Set<MaturityLevel>>() {
            }
        );
        Set<MaturityLevel> responseBody = responseEntity.getBody();
        return responseBody;
    }

    /*
     * TODO:
     *  - must complete this class with true data
     * */
}
