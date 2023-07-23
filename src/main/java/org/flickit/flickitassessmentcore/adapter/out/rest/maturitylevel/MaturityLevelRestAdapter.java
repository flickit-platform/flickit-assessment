package org.flickit.flickitassessmentcore.adapter.out.rest.maturitylevel;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.adapter.out.rest.api.DataItemsDto;
import org.flickit.flickitassessmentcore.application.port.out.maturitylevel.LoadMaturityLevelByKitPort;
import org.flickit.flickitassessmentcore.config.FlickitPlatformRestProperties;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

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
        ResponseEntity<DataItemsDto<List<MaturityLevelDto>>> responseEntity = flickitPlatformRestTemplate.exchange(
            url,
            HttpMethod.GET,
            requestEntity,
            new ParameterizedTypeReference<DataItemsDto<List<MaturityLevelDto>>>() {
            }
        );
        return MaturityLevelMapper.toResult(responseEntity.getBody().items());
    }

    record MaturityLevelDto(Long id,
                            String title,
                            Integer value,
                            @JsonProperty("LevelCompetences") List<LevelCompetenceDto> levelCompetences) {
    }

    record LevelCompetenceDto(Integer value,
                              Long maturityLevelCompetenceId) {}


}
