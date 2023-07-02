package org.flickit.flickitassessmentcore.adapter.out.rest.assessmentsubject;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.out.assessmentsubject.LoadAssessmentSubjectIdsAndQualityAttributeIdsPort;
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
public class AssessmentSubjectRestAdapter implements LoadAssessmentSubjectIdsAndQualityAttributeIdsPort {

    @Value("${flickit-platform.host}")
    private String flickitPlatformHost;

    @Override
    public ResponseParam loadByAssessmentKitId(Long assessmentKitId) {
        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
            .setConnectTimeout(Duration.ofSeconds(10))
            .setReadTimeout(Duration.ofSeconds(10))
            .messageConverters(new MappingJackson2HttpMessageConverter());
        RestTemplate restTemplate = restTemplateBuilder.build();
        String url = String.format("%s/api/internal/assessment-kit/%d/assessment-subjects/", flickitPlatformHost, assessmentKitId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Long>> requestEntity = new HttpEntity<>(null, headers);
        ResponseEntity<List<AssessmentSubjectDto>> responseEntity = restTemplate.exchange(
            url,
            HttpMethod.GET,
            requestEntity,
            new ParameterizedTypeReference<List<AssessmentSubjectDto>>() {
            }
        );
        return AssessmentSubjectMapper.toResponseParam(responseEntity.getBody());
    }

    record AssessmentSubjectDto(Long id,
                                @JsonProperty("quality_attributes") List<QualityAttributeDto> qualityAttributes) {
    }

    record QualityAttributeDto(Long id, Integer weight) {
    }
}
