package org.flickit.flickitassessmentcore.adapter.out.rest.maturitylevel;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.adapter.out.rest.api.DataItemsDto;
import org.flickit.flickitassessmentcore.adapter.out.rest.api.exception.FlickitPlatformRestException;
import org.flickit.flickitassessmentcore.application.port.out.maturitylevel.LoadMaturityLevelByKitPort;
import org.flickit.flickitassessmentcore.config.FlickitPlatformRestProperties;
import org.flickit.flickitassessmentcore.domain.MaturityLevel;
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
public class MaturityLevelRestAdapter implements LoadMaturityLevelByKitPort {

    private final RestTemplate flickitPlatformRestTemplate;
    private final FlickitPlatformRestProperties properties;

    @Override
    public Result loadByKitId(Long kitId) {
        String url = String.format(properties.getBaseUrl() + properties.getGetMaturityLevelsUrl(), kitId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Long>> requestEntity = new HttpEntity<>(null, headers);
        var responseEntity = flickitPlatformRestTemplate.exchange(
            url,
            HttpMethod.GET,
            requestEntity,
            new ParameterizedTypeReference<DataItemsDto<MaturityLevelDto>>() {
            }
        );
        if (!responseEntity.getStatusCode().is2xxSuccessful())
            throw new FlickitPlatformRestException(responseEntity.getStatusCode().value());

        List<MaturityLevel> items = responseEntity.getBody().items() != null ?
            responseEntity.getBody().items().stream()
                .map(MaturityLevelMapper::toDomainModel)
                .collect(Collectors.toList()) : List.of();
        return new LoadMaturityLevelByKitPort.Result(items);
    }

    record MaturityLevelDto(Long id,
                            String title,
                            Integer value,
                            @JsonProperty("LevelCompetences") List<LevelCompetenceDto> levelCompetences) {
    }

    record LevelCompetenceDto(Integer value,
                              Long maturityLevelCompetenceId) {}


}
