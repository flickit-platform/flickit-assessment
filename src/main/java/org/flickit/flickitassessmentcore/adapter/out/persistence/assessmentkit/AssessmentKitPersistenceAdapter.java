package org.flickit.flickitassessmentcore.adapter.out.persistence.assessmentkit;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.out.LoadAssessmentKitPort;
import org.flickit.flickitassessmentcore.domain.AssessmentKit;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@RequiredArgsConstructor
@Component
public class AssessmentKitPersistenceAdapter implements LoadAssessmentKitPort {

    /**
     * Load «Assessment Kit» from other module that contains this Entity with all its related fields (relations).
     * Get it with RestTemplateBuilder and by use of desired assessmentKitId as parameter
     * */
    @Override
    public AssessmentKit loadAssessmentKit(Long assessmentKitId) {
        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
            .setConnectTimeout(Duration.ofSeconds(10))
            .setReadTimeout(Duration.ofSeconds(10))
            .messageConverters(new MappingJackson2HttpMessageConverter());
        RestTemplate restTemplate = restTemplateBuilder.build();
        String url = "https://api.example.com/data";
        ResponseEntity<AssessmentKit> responseEntity = restTemplate.exchange(
            url,
            HttpMethod.GET,
            null,
            AssessmentKit.class
        );
        AssessmentKit responseBody = responseEntity.getBody();
        return responseBody;
    }

    /*
    * TODO:
    *  - must complete this class with true data
    * */
}
