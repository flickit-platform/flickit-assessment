package org.flickit.flickitassessmentcore.adapter.out.rest.answeroptionimpact;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.adapter.out.rest.api.DataItemsDto;
import org.flickit.flickitassessmentcore.application.port.out.answeroptionimpact.LoadAnswerOptionImpactsByAnswerOptionAndQualityAttributePort;
import org.flickit.flickitassessmentcore.config.FlickitPlatformRestProperties;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class AnswerOptionImpactRestAdapterAndQualityAttribute implements LoadAnswerOptionImpactsByAnswerOptionAndQualityAttributePort {

    private final RestTemplate flickitPlatformRestTemplate;
    private final FlickitPlatformRestProperties properties;

    @Override
    public Result loadByAnswerOptionIdAndQualityAttributeId(Long answerOptionId, Long qualityAttributeId) {
        String url = String.format(properties.getBaseUrl() + properties.getGetAnswerOptionImpactsUrl(), answerOptionId, qualityAttributeId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Long>> requestEntity = new HttpEntity<>(null, headers);
        ResponseEntity<DataItemsDto<List<AnswerOptionImpactDto>>> responseEntity = flickitPlatformRestTemplate.exchange(
            url,
            HttpMethod.GET,
            requestEntity,
            new ParameterizedTypeReference<DataItemsDto<List<AnswerOptionImpactDto>>>() {
            }
        );
        return AnswerOptionImpactMapper.toResult(responseEntity.getBody().items());
    }

    record AnswerOptionImpactDto(Long id,
                                 Long option_id,
                                 BigDecimal value,
                                 Long metric_impact_id) {
    }
}
