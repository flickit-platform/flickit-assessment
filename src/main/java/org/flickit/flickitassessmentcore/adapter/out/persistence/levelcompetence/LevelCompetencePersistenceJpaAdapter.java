package org.flickit.flickitassessmentcore.adapter.out.persistence.levelcompetence;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.flickitassessmentcore.application.port.out.levelcompetence.LoadLevelCompetenceByMaturityLevelPort;
import org.flickit.flickitassessmentcore.domain.LevelCompetence;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
@Component
@Slf4j
public class LevelCompetencePersistenceJpaAdapter implements LoadLevelCompetenceByMaturityLevelPort {

    @Value("${app.flickit-platform.rest.base-url}")
    private String flickitPlatformHost;

    @Override
    public Result loadLevelCompetenceByMaturityLevelId(Param param) {
        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
            .setConnectTimeout(Duration.ofSeconds(10))
            .setReadTimeout(Duration.ofSeconds(10))
            .messageConverters(new MappingJackson2HttpMessageConverter());
        RestTemplate restTemplate = restTemplateBuilder.build();
        String url = String.format("%s/api/internal/maturity-level/%d/level-competences", flickitPlatformHost, param.maturityLevelId());
        log.warn(url);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Long>> requestEntity = new HttpEntity<>(null, headers);
        ResponseEntity<ResponseDto> responseEntity = restTemplate.exchange(
            url,
            HttpMethod.GET,
            requestEntity,
            new ParameterizedTypeReference<ResponseDto>() {
            }
        );
        return LevelCompetenceMapper.toResult(responseEntity.getBody().items);
    }

    record ResponseDto(@JsonProperty("items") List<LevelCompetenceDto> items) {}

    record LevelCompetenceDto(Long id,
                              Long maturityLevelId,
                              Integer value,
                              Long maturityLevelCompetenceId) {}

}
