package org.flickit.flickitassessmentcore.adapter.out.rest.answeroptionimpact;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.adapter.out.rest.api.DataItemsDto;
import org.flickit.flickitassessmentcore.adapter.out.rest.api.exception.FlickitPlatformRestException;
import org.flickit.flickitassessmentcore.application.port.out.answeroptionimpact.LoadAnswerOptionImpactsByAnswerOptionAndQualityAttributePort;
import org.flickit.flickitassessmentcore.config.FlickitPlatformRestProperties;
import org.flickit.flickitassessmentcore.domain.AnswerOptionImpact;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        var responseEntity = flickitPlatformRestTemplate.exchange(
            url,
            HttpMethod.GET,
            requestEntity,
            new ParameterizedTypeReference<DataItemsDto<AnswerOptionImpactDto>>() {
            }
        );
        if (!responseEntity.getStatusCode().is2xxSuccessful())
            throw new FlickitPlatformRestException(responseEntity.getStatusCode().value());

        List<AnswerOptionImpact> items = responseEntity.getBody().items() != null ?
            responseEntity.getBody().items().stream()
                .map(AnswerOptionImpactMapper::toDomainModel)
                .collect(Collectors.toList()) : List.of();
        return new LoadAnswerOptionImpactsByAnswerOptionAndQualityAttributePort.Result(items);
    }

    record AnswerOptionImpactDto(Long id,
                                 Long option_id,
                                 Double value,
                                 Long metric_impact_id) {
    }
}
