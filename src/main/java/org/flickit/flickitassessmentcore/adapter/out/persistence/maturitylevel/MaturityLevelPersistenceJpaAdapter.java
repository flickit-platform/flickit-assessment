package org.flickit.flickitassessmentcore.adapter.out.persistence.maturitylevel;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.out.maturitylevel.LoadMaturityLevelByKitPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class MaturityLevelPersistenceJpaAdapter implements LoadMaturityLevelByKitPort {

    @Value("${flickit-platform.host}")
    private String flickitPlatformHost;

    @Override
    public Result loadMaturityLevelByKitId(Param param) {
        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
            .setConnectTimeout(Duration.ofSeconds(10))
            .setReadTimeout(Duration.ofSeconds(10))
            .messageConverters(new MappingJackson2HttpMessageConverter());
        RestTemplate restTemplate = restTemplateBuilder.build();
        String url = String.format("%s/api/internal/assessmentkit/%d/maturitylevel", flickitPlatformHost, param.kitId());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Long>> requestEntity = new HttpEntity<>(null, headers);
        ResponseEntity<List<MaturityLevelDto>> responseEntity = restTemplate.exchange(
            url,
            HttpMethod.GET,
            requestEntity,
            new ParameterizedTypeReference<List<MaturityLevelDto>>() {
            }
        );
        return MaturityLevelMapper.toResult(responseEntity.getBody());
    }

    record MaturityLevelDto(Long id,
                            String title,
                            Integer value) {
    }


}
