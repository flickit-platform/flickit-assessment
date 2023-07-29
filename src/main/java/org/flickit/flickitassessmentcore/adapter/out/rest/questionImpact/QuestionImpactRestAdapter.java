package org.flickit.flickitassessmentcore.adapter.out.rest.questionImpact;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.adapter.out.rest.api.exception.FlickitPlatformRestException;
import org.flickit.flickitassessmentcore.application.port.out.questionImpact.LoadQuestionImpactPort;
import org.flickit.flickitassessmentcore.config.FlickitPlatformRestProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
@Component
public class QuestionImpactRestAdapter implements LoadQuestionImpactPort {

    private final RestTemplate flickitPlatformRestTemplate;
    private final FlickitPlatformRestProperties properties;

    @Override
    public LoadQuestionImpactPort.Result load(Long id) {
        String url = String.format(properties.getBaseUrl() + properties.getGetQuestionImpactByIdUrl(), id);
        var responseEntity = flickitPlatformRestTemplate.getForEntity(url, QuestionImpactDto.class);
        if (!responseEntity.getStatusCode().is2xxSuccessful())
            throw new FlickitPlatformRestException(responseEntity.getStatusCode().value());
        return new LoadQuestionImpactPort.Result(QuestionImpactMapper.toDomainModel(responseEntity.getBody()));
    }
}
