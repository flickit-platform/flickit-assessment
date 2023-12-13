package org.flickit.assessment.core.adapter.out.rest.maturitylevel;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.adapter.out.rest.api.DataItemsDto;
import org.flickit.assessment.common.adapter.out.rest.api.exception.FlickitPlatformRestException;
import org.flickit.assessment.common.config.FlickitPlatformRestProperties;
import org.flickit.assessment.core.application.domain.MaturityLevel;
import org.flickit.assessment.core.application.port.out.maturitylevel.LoadMaturityLevelsByKitPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@RequiredArgsConstructor
public class MaturityLevelRestAdapter implements LoadMaturityLevelsByKitPort {

    private final RestTemplate flickitPlatformRestTemplate;
    private final FlickitPlatformRestProperties properties;

    @Override
    public List<MaturityLevel> loadByKitId(Long kitId) {
        return loadMaturityLevelsDtoByKitId(kitId).stream()
            .map(MaturityLevelDto::dtoToDomain)
            .toList();
    }

    public List<MaturityLevelDto> loadMaturityLevelsDtoByKitId(Long kitId) {
        String url = properties.getBaseUrl() + String.format(properties.getGetMaturityLevelsUrl(), kitId);
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

        return responseEntity.getBody() != null && responseEntity.getBody().items() != null ?
            responseEntity.getBody().items() :
            List.of();
    }
}
