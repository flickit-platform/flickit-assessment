package org.flickit.flickitassessmentcore.adapter.out.rest.question;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.adapter.out.rest.api.DataItemsDto;
import org.flickit.flickitassessmentcore.adapter.out.rest.api.exception.FlickitPlatformRestException;
import org.flickit.flickitassessmentcore.adapter.out.rest.questionImpact.QuestionImpactDto;
import org.flickit.flickitassessmentcore.application.port.out.question.LoadQuestionsByQualityAttributePort;
import org.flickit.flickitassessmentcore.config.FlickitPlatformRestProperties;
import org.flickit.flickitassessmentcore.domain.Question;
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
public class QuestionRestAdapter implements LoadQuestionsByQualityAttributePort {

    private final RestTemplate flickitPlatformRestTemplate;
    private final FlickitPlatformRestProperties properties;

    @Override
    public Result loadByQualityAttributeId(Param param) {
        String url = String.format(properties.getBaseUrl() + properties.getGetQuestionsUrl(), param.qualityAttributeId());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Long>> requestEntity = new HttpEntity<>(null, headers);
        var responseEntity = flickitPlatformRestTemplate.exchange(
            url,
            HttpMethod.GET,
            requestEntity,
            new ParameterizedTypeReference<DataItemsDto<QuestionDto>>() {
            }
        );
        if (!responseEntity.getStatusCode().is2xxSuccessful())
            throw new FlickitPlatformRestException(responseEntity.getStatusCode().value());

        List<Question> items = responseEntity.getBody().items() != null ?
            responseEntity.getBody().items().stream()
                .map(QuestionMapper::toDomainModel)
                .collect(Collectors.toList()) : List.of();
        return new LoadQuestionsByQualityAttributePort.Result(items);
    }

    record QuestionDto(Long id,
                       String title,
                       Integer index,
                       @JsonProperty("question-impacts") List<QuestionImpactDto> questionImpacts) {
    }

}
