package org.flickit.assessment.core.adapter.out.rest.answeroption;

import lombok.AllArgsConstructor;
import org.flickit.assessment.common.adapter.out.rest.api.DataItemsDto;
import org.flickit.assessment.common.adapter.out.rest.api.exception.FlickitPlatformRestException;
import org.flickit.assessment.common.config.FlickitPlatformRestProperties;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@AllArgsConstructor
public class AnswerOptionRestAdapter {

    private final RestTemplate flickitPlatformRestTemplate;
    private final FlickitPlatformRestProperties properties;

    public List<AnswerOptionDto> loadAnswerOptionByIds(List<Long> answerOptionIds) {
        int limit = 100;
        int count = answerOptionIds.size();
        String commaSeparatedIds;
        List<AnswerOptionDto> fetchedAnswerOptions = new ArrayList<>();

        int from = 0;
        while (from < count) {
            List<Long> limitedSubList = answerOptionIds.subList(from, Math.min(from + limit, count));
            commaSeparatedIds = limitedSubList.stream()
                .filter(Objects::nonNull)
                .map(String::valueOf)
                .collect(Collectors.joining(","));
            fetchedAnswerOptions.addAll(loadAnswerOptions(commaSeparatedIds));
            from += limit;
        }

        return fetchedAnswerOptions;
    }

    private List<AnswerOptionDto> loadAnswerOptions(String commaSeparatedIds) {
        String url = properties.getBaseUrl() + String.format(properties.getGetAnswerOptionsUrl(), commaSeparatedIds);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Long>> requestEntity = new HttpEntity<>(null, headers);
        var responseEntity = flickitPlatformRestTemplate.exchange(
            url,
            HttpMethod.GET,
            requestEntity,
            new ParameterizedTypeReference<DataItemsDto<AnswerOptionDto>>() {
            }
        );
        if (!responseEntity.getStatusCode().is2xxSuccessful())
            throw new FlickitPlatformRestException(responseEntity.getStatusCode().value());

        return responseEntity.getBody() != null && responseEntity.getBody().items() != null ?
            responseEntity.getBody().items() :
            List.of();
    }
}
