package org.flickit.flickitassessmentcore.adapter.out.rest.levelcompetence;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.adapter.out.rest.api.DataItemsDto;
import org.flickit.flickitassessmentcore.application.port.out.levelcompetence.LoadLevelCompetenceByMaturityLevelPort;
import org.flickit.flickitassessmentcore.config.FlickitPlatformRestProperties;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

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
        ResponseEntity<DataItemsDto<List<LevelCompetenceDto>>> responseEntity = flickitPlatformRestTemplate.exchange(
            url,
            HttpMethod.GET,
            requestEntity,
            new ParameterizedTypeReference<DataItemsDto<List<LevelCompetenceDto>>>() {
            }
        );
        return LevelCompetenceMapper.toResult(responseEntity.getBody().items());
    }

    record LevelCompetenceDto(Long id,
                              Long maturityLevelId,
                              Integer value,
                              Long maturityLevelCompetenceId) {
    }

}
