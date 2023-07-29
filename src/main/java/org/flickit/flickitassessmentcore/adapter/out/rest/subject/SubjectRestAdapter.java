package org.flickit.flickitassessmentcore.adapter.out.rest.subject;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.adapter.out.rest.api.DataItemsDto;
import org.flickit.flickitassessmentcore.adapter.out.rest.api.exception.FlickitPlatformRestException;
import org.flickit.flickitassessmentcore.application.port.out.subject.LoadSubjectIdsAndQualityAttributeIdsPort;
import org.flickit.flickitassessmentcore.config.FlickitPlatformRestProperties;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class SubjectRestAdapter implements LoadSubjectIdsAndQualityAttributeIdsPort {

    private final RestTemplate flickitPlatformRestTemplate;
    private final FlickitPlatformRestProperties properties;

    @Override
    public ResponseParam loadByAssessmentKitId(Long assessmentKitId) {
        String url = String.format(properties.getBaseUrl() + properties.getGetSubjectIdsUrl(), assessmentKitId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Long>> requestEntity = new HttpEntity<>(null, headers);
        var responseEntity = flickitPlatformRestTemplate.exchange(
            url,
            HttpMethod.GET,
            requestEntity,
            new ParameterizedTypeReference<DataItemsDto<SubjectDto>>() {
            }
        );
        if (!responseEntity.getStatusCode().is2xxSuccessful())
            throw new FlickitPlatformRestException(responseEntity.getStatusCode().value());

        return SubjectMapper.toResponseParam(responseEntity.getBody().items());
    }

    record SubjectDto(Long id,
                      @JsonProperty("quality_attributes") List<QualityAttributeDto> qualityAttributes) {
    }

    record QualityAttributeDto(Long id, Integer weight) {
    }
}
