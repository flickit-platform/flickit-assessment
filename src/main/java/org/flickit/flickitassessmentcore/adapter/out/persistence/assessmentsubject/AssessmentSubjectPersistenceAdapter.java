package org.flickit.flickitassessmentcore.adapter.out.persistence.assessmentsubject;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.out.assessmentsubject.LoadAssessmentSubjectByAssessmentKitPort;
import org.flickit.flickitassessmentcore.application.port.out.assessmentsubject.LoadSubjectPort;
import org.flickit.flickitassessmentcore.domain.AssessmentSubject;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.List;

@RequiredArgsConstructor
@Component
public class AssessmentSubjectPersistenceAdapter implements LoadSubjectPort, LoadAssessmentSubjectByAssessmentKitPort {

    /**
     * Load «Assessment Subject» from other module that contains this Entity with all its related fields (relations).
     * Get it with RestTemplateBuilder and by use of desired assessmentSubjectId as parameter
     * */

    @Override
    public AssessmentSubject loadSubject(Long subId) {
        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
            .setConnectTimeout(Duration.ofSeconds(10))
            .setReadTimeout(Duration.ofSeconds(10))
            .messageConverters(new MappingJackson2HttpMessageConverter());
        RestTemplate restTemplate = restTemplateBuilder.build();
        String url = "https://api.example.com/data";
        ResponseEntity<AssessmentSubject> responseEntity = restTemplate.exchange(
            url,
            HttpMethod.GET,
            null,
            AssessmentSubject.class
        );
        AssessmentSubject responseBody = responseEntity.getBody();
        return responseBody;
    }

    @Override
    public List<AssessmentSubject> loadSubjectByKitId(Long kitId) {
        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
            .setConnectTimeout(Duration.ofSeconds(10))
            .setReadTimeout(Duration.ofSeconds(10))
            .messageConverters(new MappingJackson2HttpMessageConverter());
        RestTemplate restTemplate = restTemplateBuilder.build();
        String url = "https://api.example.com/data";
        ResponseEntity<List<AssessmentSubject>> responseEntity = restTemplate.exchange(
            url,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<AssessmentSubject>>() {
            }
        );
        List<AssessmentSubject> responseBody = responseEntity.getBody();
        return responseBody;
    }

    /*
    * TODO:
    *  - must complete this class with true data
    * */
}
