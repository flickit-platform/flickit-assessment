package org.flickit.flickitassessmentcore.adapter.out.persistence.answeroptionimpact;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.out.answeroptionimpact.LoadAnswerOptionImpactsByAnswerOptionPort;
import org.flickit.flickitassessmentcore.domain.AnswerOptionImpact;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
@Component
public class AnswerOptionImpactPersistenceJpaAdapter implements LoadAnswerOptionImpactsByAnswerOptionPort {

    @Value("${flickit-platform.host}")
    private String flickitPlatformHost;

    @Override
    public Result findAnswerOptionImpactsByAnswerOptionId(Param param) {
        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
            .setConnectTimeout(Duration.ofSeconds(10))
            .setReadTimeout(Duration.ofSeconds(10))
            .messageConverters(new MappingJackson2HttpMessageConverter());
        RestTemplate restTemplate = restTemplateBuilder.build();
        String url = String.format("%s/api/internal/answertemplate/%d/optionvalue", flickitPlatformHost, param.answerOptionId());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Long>> requestEntity = new HttpEntity<>(null, headers);
        ResponseEntity<List<AnswerOptionImpactDto>> responseEntity = restTemplate.exchange(
            url,
            HttpMethod.GET,
            requestEntity,
            new ParameterizedTypeReference<List<AnswerOptionImpactDto>>() {
            }
        );
        return AnswerOptionImpactMapper.toResult(responseEntity.getBody());
    }

    record AnswerOptionImpactDto(Long id,
                                 Long optionId,
                                 BigDecimal value,
                                 Long questionImpactId) {}
}
