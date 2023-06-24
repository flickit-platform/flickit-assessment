package org.flickit.flickitassessmentcore.adapter.out.rest.assessmentsubject;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.out.assessmentsubject.LoadAssessmentSubjectIdsAndQualityAttributeIdsPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class AssessmentSubjectRestAdapter implements LoadAssessmentSubjectIdsAndQualityAttributeIdsPort {

    @Override
    public ResponseParam loadByAssessmentKitId(Long assessmentKitId) {
        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
            .setConnectTimeout(Duration.ofSeconds(10))
            .setReadTimeout(Duration.ofSeconds(10))
            .messageConverters(new MappingJackson2HttpMessageConverter());
        RestTemplate restTemplate = restTemplateBuilder.build();
        String url = "https://api.example.com/data";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Long> requestBody = new HashMap<>();
        requestBody.put("kitId", assessmentKitId);
        HttpEntity<Map<String, Long>> requestEntity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<LoadAssessmentSubjectIdsAndQualityAttributeIdsPort.ResponseParam> responseEntity = restTemplate.exchange(
            url,
            HttpMethod.GET,
            requestEntity,
            new ParameterizedTypeReference<LoadAssessmentSubjectIdsAndQualityAttributeIdsPort.ResponseParam>() {
            }
        );
        return responseEntity.getBody();
    }
}
