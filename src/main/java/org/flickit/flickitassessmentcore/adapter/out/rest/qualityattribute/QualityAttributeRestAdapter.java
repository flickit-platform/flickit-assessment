package org.flickit.flickitassessmentcore.adapter.out.rest.qualityattribute;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.adapter.out.rest.api.DataItemsDto;
import org.flickit.flickitassessmentcore.application.port.out.qualityattribute.LoadQualityAttributeBySubjectPort;
import org.flickit.flickitassessmentcore.config.FlickitPlatformRestProperties;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class QualityAttributeRestAdapter implements LoadQualityAttributeBySubjectPort {

    private final RestTemplate flickitPlatformRestTemplate;
    private final FlickitPlatformRestProperties properties;

    @Override
    public Result loadBySubjectId(Long subjectId) {
        String url = String.format(properties.getBaseUrl() + properties.getGetQualityAttributesUrl(), subjectId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Long>> requestEntity = new HttpEntity<>(null, headers);
        ResponseEntity<DataItemsDto<List<QualityAttributeDto>>> responseEntity = flickitPlatformRestTemplate.exchange(
            url,
            HttpMethod.GET,
            requestEntity,
            new ParameterizedTypeReference<DataItemsDto<List<QualityAttributeDto>>>() {
            }
        );
        return QualityAttributeMapper.toResult(responseEntity.getBody().items());
    }

    record QualityAttributeDto(Long id,
                               Integer weight) {
    }

}
