package org.flickit.flickitassessmentcore.adapter.out.persistence.levelcompetence;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.out.LoadLevelCompetenceByMLPort;
import org.flickit.flickitassessmentcore.domain.LevelCompetence;
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
public class LevelCompetencePersistenceAdapter implements LoadLevelCompetenceByMLPort {
    @Override
    public Set<LevelCompetence> loadLevelCompetenceByMLId(Long mlId) {
        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
            .setConnectTimeout(Duration.ofSeconds(10))
            .setReadTimeout(Duration.ofSeconds(10))
            .messageConverters(new MappingJackson2HttpMessageConverter());
        RestTemplate restTemplate = restTemplateBuilder.build();
        String url = "https://api.example.com/data";
        ResponseEntity<Set<LevelCompetence>> responseEntity = restTemplate.exchange(
            url,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<Set<LevelCompetence>>() {
            }
        );
        Set<LevelCompetence> responseBody = responseEntity.getBody();
        return responseBody;
    }

    /*
     * TODO:
     *  - must complete this class with true data
     * */
}
