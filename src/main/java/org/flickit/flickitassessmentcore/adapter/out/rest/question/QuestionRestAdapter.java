package org.flickit.flickitassessmentcore.adapter.out.rest.question;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.adapter.out.rest.api.DataItemsDto;
import org.flickit.flickitassessmentcore.application.port.out.question.LoadQuestionsByQualityAttributePort;
import org.flickit.flickitassessmentcore.config.FlickitPlatformRestProperties;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class QuestionRestAdapter implements LoadQuestionsByQualityAttributePort {

    private final RestTemplate flickitPlatformRestTemplate;
    private final FlickitPlatformRestProperties properties;

    @Override
    public Result loadQuestionsByQualityAttributeId(Param param) {
        String url = String.format(properties.getBaseUrl() + properties.getGetQuestionsUrl(), param.qualityAttributeId());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Long>> requestEntity = new HttpEntity<>(null, headers);
        ResponseEntity<DataItemsDto<List<QuestionDto>>> responseEntity = flickitPlatformRestTemplate.exchange(
            url,
            HttpMethod.GET,
            requestEntity,
            new ParameterizedTypeReference<DataItemsDto<List<QuestionDto>>>() {
            }
        );
        return QuestionMapper.toResult(responseEntity.getBody().items());
    }

    record QuestionDto(Long id,
                       String title,
                       Integer index,
                       @JsonProperty("quality_attributes") List<QualityAttributeDto> qualityAttributes) {
    }

    public record QualityAttributeDto(Long id,
                                      String code,
                                      String title,
                                      String description,
                                      Integer index) {
    }


}
