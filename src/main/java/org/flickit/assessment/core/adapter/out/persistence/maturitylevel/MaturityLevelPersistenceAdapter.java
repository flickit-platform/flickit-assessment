package org.flickit.assessment.core.adapter.out.persistence.maturitylevel;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.port.out.maturitylevel.LoadMaturityLevelByKitPort;
import org.flickit.assessment.core.domain.MaturityLevel;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
@Component
public class MaturityLevelPersistenceAdapter implements LoadMaturityLevelByKitPort {
    @Override
    public Set<MaturityLevel> loadMaturityLevelByKitId(Long kitId) {
        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
            .setConnectTimeout(Duration.ofSeconds(10))
            .setReadTimeout(Duration.ofSeconds(10))
            .messageConverters(new MappingJackson2HttpMessageConverter());
        RestTemplate restTemplate = restTemplateBuilder.build();
        String url = "https://api.example.com/data";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Long> requestBody = new HashMap<>();
        requestBody.put("kitId", kitId);
        HttpEntity<Map<String, Long>> requestEntity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<Set<MaturityLevel>> responseEntity = restTemplate.exchange(
            url,
            HttpMethod.GET,
            requestEntity,
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
