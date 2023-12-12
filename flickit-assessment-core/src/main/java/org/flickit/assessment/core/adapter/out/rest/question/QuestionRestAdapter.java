package org.flickit.assessment.core.adapter.out.rest.question;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.adapter.out.rest.api.DataItemsDto;
import org.flickit.assessment.common.adapter.out.rest.api.PaginatedDataItemsDto;
import org.flickit.assessment.common.adapter.out.rest.api.exception.FlickitPlatformRestException;
import org.flickit.assessment.common.config.FlickitPlatformRestProperties;
import org.flickit.assessment.core.application.domain.Question;
import org.flickit.assessment.core.application.port.out.question.LoadQuestionsBySubjectPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@RequiredArgsConstructor
public class QuestionRestAdapter implements LoadQuestionsBySubjectPort {

    private final RestTemplate flickitPlatformRestTemplate;
    private final FlickitPlatformRestProperties properties;

    public List<QuestionDto> loadByAssessmentKitId(long assessmentKitId) {
        int count;
        int page = 1;

        PaginatedDataItemsDto<QuestionDto> responseDto = loadByPage(assessmentKitId, page);

        if (responseDto == null)
            return new ArrayList<>();

        count = responseDto.count();

        List<QuestionDto> fetchedQuestions = new ArrayList<>(responseDto.items() != null ?
            responseDto.items() : List.of());

        while (responseDto.next() != null && fetchedQuestions.size() < count) {
            page++;
            responseDto = loadByPage(assessmentKitId, page);
            if (responseDto == null)
                break;
            fetchedQuestions.addAll(responseDto.items());
        }
        return fetchedQuestions;
    }

    private PaginatedDataItemsDto<QuestionDto> loadByPage(long assessmentKitId, int page) {
        String url = properties.getBaseUrl() + String.format(properties.getGetQuestionsUrl(), assessmentKitId, page);
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
    public List<Question> loadQuestionsBySubject(long subjectId) {
        String url = properties.getBaseUrl() + String.format(properties.getGetQuestionsBySubjectUrl(), subjectId);
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

        DataItemsDto<QuestionDto> responseEntityBody = responseEntity.getBody();
        return responseEntityBody != null ? responseEntityBody.items().stream()
            .map(x -> new Question(x.id(), new ArrayList<>()))
            .toList() :
            List.of();
    }
}
