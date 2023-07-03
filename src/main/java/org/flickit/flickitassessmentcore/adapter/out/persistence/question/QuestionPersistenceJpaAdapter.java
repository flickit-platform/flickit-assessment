package org.flickit.flickitassessmentcore.adapter.out.persistence.question;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.out.question.LoadQuestionsByQualityAttributePort;
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
public class QuestionPersistenceJpaAdapter implements LoadQuestionsByQualityAttributePort {

    @Value("${flickit-platform.host}")
    private String flickitPlatformHost;

    @Override
    public Result loadQuestionsByQualityAttributeId(Param param) {
        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
            .setConnectTimeout(Duration.ofSeconds(10))
            .setReadTimeout(Duration.ofSeconds(10))
            .messageConverters(new MappingJackson2HttpMessageConverter());
        RestTemplate restTemplate = restTemplateBuilder.build();
        String url = String.format("%s/api/internal/qualityattribute/%d/metric", flickitPlatformHost, param.qualityAttributeId());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Long>> requestEntity = new HttpEntity<>(null, headers);
        ResponseEntity<List<QuestionDto>> responseEntity = restTemplate.exchange(
            url,
            HttpMethod.GET,
            requestEntity,
            new ParameterizedTypeReference<List<QuestionDto>>() {
            }
        );
        return QuestionMapper.toResult(responseEntity.getBody());
    }

    record QuestionDto(Long id,
                       String title,
                       Integer index,
                       @JsonProperty("quality_attributes") List<QualityAttributeDto> qualityAttributes) {}

    public record QualityAttributeDto(Long id,
                               String code,
                               String title,
                               String description,
                               Integer index) {
    }


}
