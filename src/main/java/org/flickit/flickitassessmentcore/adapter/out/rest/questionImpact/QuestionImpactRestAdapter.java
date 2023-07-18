package org.flickit.flickitassessmentcore.adapter.out.rest.questionImpact;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.adapter.out.rest.api.DataItemsDto;
import org.flickit.flickitassessmentcore.application.port.out.questionImpact.LoadQuestionImpactPort;
import org.flickit.flickitassessmentcore.config.FlickitPlatformRestProperties;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class QuestionImpactRestAdapter implements LoadQuestionImpactPort {

    private final RestTemplate flickitPlatformRestTemplate;
    private final FlickitPlatformRestProperties properties;

    @Override
    public Result load(Param param) {
        String url = String.format(properties.getBaseUrl() + properties.getGetQuestionImpactsUrl(), param.id());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Long>> requestEntity = new HttpEntity<>(null, headers);
        ResponseEntity<DataItemsDto<List<QuestionImpactDto>>> responseEntity = flickitPlatformRestTemplate.exchange(
            url,
            HttpMethod.GET,
            requestEntity,
            new ParameterizedTypeReference<DataItemsDto<List<QuestionImpactDto>>>() {
            }
        );
        return QuestionImpactMapper.toResult(responseEntity.getBody().items());
    }

    record QuestionImpactDto(Long id,
                             Integer level,
                             Integer weight,
                             Long maturity_level,
                             Long metric,
                             Long quality_attribute) {
    }
}
