package org.flickit.flickitassessmentcore.adapter.out.persistence.question;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.out.LoadQuestionsByQAIdPort;
import org.flickit.flickitassessmentcore.domain.AnswerOptionImpact;
import org.flickit.flickitassessmentcore.domain.Question;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.Set;

@RequiredArgsConstructor
@Component
public class QuestionPersistenceAdapter implements LoadQuestionsByQAIdPort {
    @Override
    public Set<Question> loadQuestionsByQualityAttributeId(Long qualityAttributeId) {
        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
            .setConnectTimeout(Duration.ofSeconds(10))
            .setReadTimeout(Duration.ofSeconds(10))
            .messageConverters(new MappingJackson2HttpMessageConverter());
        RestTemplate restTemplate = restTemplateBuilder.build();
        String url = "https://api.example.com/data";
        ResponseEntity<Set<Question>> responseEntity = restTemplate.exchange(
            url,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<Set<Question>>() {
            }
        );
        Set<Question> responseBody = responseEntity.getBody();
        return responseBody;
    }

    /*
     * TODO:
     *  - must complete this class with true data
     * */
}
