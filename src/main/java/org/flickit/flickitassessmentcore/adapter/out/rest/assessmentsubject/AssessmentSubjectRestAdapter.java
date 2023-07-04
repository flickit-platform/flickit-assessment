package org.flickit.flickitassessmentcore.adapter.out.rest.assessmentsubject;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.out.assessmentsubject.LoadAssessmentSubjectIdsAndQualityAttributeIdsPort;
import org.flickit.flickitassessmentcore.config.FlickitPlatformRestProperties;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AssessmentSubjectRestAdapter implements LoadAssessmentSubjectIdsAndQualityAttributeIdsPort {

    private final RestTemplate flickitPlatformRestTemplate;
    private final FlickitPlatformRestProperties properties;

    @Override
    public ResponseParam loadByAssessmentKitId(Long assessmentKitId) {
        String url = String.format(properties.getBaseUrl() +properties.getGetSubjectIdsUrl(), assessmentKitId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Long>> requestEntity = new HttpEntity<>(null, headers);
        ResponseEntity<List<AssessmentSubjectDto>> responseEntity = flickitPlatformRestTemplate.exchange(
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
