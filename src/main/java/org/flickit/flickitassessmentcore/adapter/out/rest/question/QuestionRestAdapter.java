package org.flickit.flickitassessmentcore.adapter.out.rest.question;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.adapter.out.rest.api.PaginatedDataItemsDto;
import org.flickit.flickitassessmentcore.adapter.out.rest.api.exception.FlickitPlatformRestException;
import org.flickit.flickitassessmentcore.application.domain.Question;
import org.flickit.flickitassessmentcore.application.port.out.question.LoadQuestionsBySubjectPort;
import org.flickit.flickitassessmentcore.config.FlickitPlatformRestProperties;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class QuestionRestAdapter implements LoadQuestionsBySubjectPort {

    private final RestTemplate flickitPlatformRestTemplate;
    private final FlickitPlatformRestProperties properties;

    public List<QuestionDto> loadByAssessmentKitId(long assessmentKitId) {
        int count;
        int page = 1;
        List<QuestionDto> fetchedQuestions = new ArrayList<>();

        PaginatedDataItemsDto<QuestionDto> responseDto = loadByPage(assessmentKitId, page);

        count = responseDto.count();

        fetchedQuestions.addAll(responseDto.items() != null ?
            responseDto.items() : List.of());

        while (responseDto.next() != null && fetchedQuestions.size() < count) {
            page++;
            responseDto = loadByPage(assessmentKitId, page);
            fetchedQuestions.addAll(responseDto.items());
        }
        return fetchedQuestions;
    }

    private PaginatedDataItemsDto<QuestionDto> loadByPage(long assessmentKitId, int page) {
        String url = String.format(properties.getBaseUrl() + properties.getGetQuestionsUrl(), assessmentKitId, page);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Long>> requestEntity = new HttpEntity<>(null, headers);
        var responseEntity = flickitPlatformRestTemplate.exchange(
            url,
            HttpMethod.GET,
            requestEntity,
            new ParameterizedTypeReference<PaginatedDataItemsDto<QuestionDto>>() {
            }
        );
        if (!responseEntity.getStatusCode().is2xxSuccessful())
            throw new FlickitPlatformRestException(responseEntity.getStatusCode().value());

        return responseEntity.getBody();
    }

    @Override
    public List<Question> loadImpactfulQuestionsBySubjectId(long subjectId) {
        String url = String.format(properties.getBaseUrl() + properties.getGetQuestionsBySubjectUrl(), subjectId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Long>> requestEntity = new HttpEntity<>(null, headers);
        var responseEntity = flickitPlatformRestTemplate.exchange(
            url,
            HttpMethod.GET,
            requestEntity,
            new ParameterizedTypeReference<List<ImpactfulQuestionDto>>() {
            }
        );
        if (!responseEntity.getStatusCode().is2xxSuccessful())
            throw new FlickitPlatformRestException(responseEntity.getStatusCode().value());

        List<ImpactfulQuestionDto> responseEntityBody = responseEntity.getBody();
        return responseEntityBody != null ? responseEntityBody.stream()
            .map(QuestionMapper::toDomainModel)
            .toList() :
            List.of();
    }
}
