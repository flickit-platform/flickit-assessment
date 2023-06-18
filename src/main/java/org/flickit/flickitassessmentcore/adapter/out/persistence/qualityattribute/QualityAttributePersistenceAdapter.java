package org.flickit.flickitassessmentcore.adapter.out.persistence.qualityattribute;

import org.flickit.flickitassessmentcore.application.port.out.qualityattribute.LoadQualityAttributeBySubPort;
import org.flickit.flickitassessmentcore.application.port.out.qualityattribute.LoadQualityAttributePort;
import org.flickit.flickitassessmentcore.domain.QualityAttribute;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.List;

@Component
public class QualityAttributePersistenceAdapter implements LoadQualityAttributePort, LoadQualityAttributeBySubPort {
    @Override
    public QualityAttribute loadQualityAttribute(Long qualityAttributeId) {
        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
            .setConnectTimeout(Duration.ofSeconds(10))
            .setReadTimeout(Duration.ofSeconds(10))
            .messageConverters(new MappingJackson2HttpMessageConverter());
        RestTemplate restTemplate = restTemplateBuilder.build();
        String url = "https://api.example.com/data";
        ResponseEntity<QualityAttribute> responseEntity = restTemplate.exchange(
            url,
            HttpMethod.GET,
            null,
            QualityAttribute.class
        );
        QualityAttribute responseBody = responseEntity.getBody();
        return responseBody;
    }

    @Override
    public List<QualityAttribute> loadQABySubId(Long subId) {
        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
            .setConnectTimeout(Duration.ofSeconds(10))
            .setReadTimeout(Duration.ofSeconds(10))
            .messageConverters(new MappingJackson2HttpMessageConverter());
        RestTemplate restTemplate = restTemplateBuilder.build();
        String url = "https://api.example.com/data";
        ResponseEntity<List<QualityAttribute>> responseEntity = restTemplate.exchange(
            url,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<QualityAttribute>>() {
            }
        );
        List<QualityAttribute> responseBody = responseEntity.getBody();
        return responseBody;
    }
}
