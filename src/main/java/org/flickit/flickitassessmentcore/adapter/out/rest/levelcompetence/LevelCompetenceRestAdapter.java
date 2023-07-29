package org.flickit.flickitassessmentcore.adapter.out.rest.levelcompetence;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.adapter.out.rest.api.DataItemsDto;
import org.flickit.flickitassessmentcore.adapter.out.rest.api.exception.FlickitPlatformRestException;
import org.flickit.flickitassessmentcore.application.port.out.levelcompetence.LoadLevelCompetenceByMaturityLevelPort;
import org.flickit.flickitassessmentcore.config.FlickitPlatformRestProperties;
import org.flickit.flickitassessmentcore.domain.LevelCompetence;
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
public class LevelCompetenceRestAdapter implements LoadLevelCompetenceByMaturityLevelPort {

    private final RestTemplate flickitPlatformRestTemplate;
    private final FlickitPlatformRestProperties properties;

    @Override
    public Result loadByMaturityLevelId(Long maturityLevelId) {
        String url = String.format(properties.getBaseUrl() + properties.getGetLevelCompetencesUrl(), maturityLevelId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Long>> requestEntity = new HttpEntity<>(null, headers);
        var responseEntity = flickitPlatformRestTemplate.exchange(
            url,
            HttpMethod.GET,
            requestEntity,
            new ParameterizedTypeReference<DataItemsDto<LevelCompetenceDto>>() {
            }
        );
        if (!responseEntity.getStatusCode().is2xxSuccessful())
            throw new FlickitPlatformRestException(responseEntity.getStatusCode().value());

        List<LevelCompetence> items = responseEntity.getBody().items() != null ?
            responseEntity.getBody().items().stream()
                .map(LevelCompetenceMapper::toDomainModel)
                .collect(Collectors.toList()) : List.of();
        return new LoadLevelCompetenceByMaturityLevelPort.Result(items);
    }

    record LevelCompetenceDto(Long id,
                              Long maturityLevelId,
                              Integer value,
                              Long maturityLevelCompetenceId) {
    }

}
