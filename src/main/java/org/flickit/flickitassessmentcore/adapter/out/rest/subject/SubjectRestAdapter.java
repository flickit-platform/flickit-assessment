package org.flickit.flickitassessmentcore.adapter.out.rest.subject;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.adapter.out.rest.answeroption.AnswerOptionDto;
import org.flickit.flickitassessmentcore.adapter.out.rest.api.DataItemsDto;
import org.flickit.flickitassessmentcore.adapter.out.rest.api.exception.FlickitPlatformRestException;
import org.flickit.flickitassessmentcore.adapter.out.rest.question.QuestionDto;
import org.flickit.flickitassessmentcore.adapter.out.rest.question.QuestionRestAdapter;
import org.flickit.flickitassessmentcore.application.domain.Answer;
import org.flickit.flickitassessmentcore.application.domain.Question;
import org.flickit.flickitassessmentcore.application.port.out.subject.GetSubjectAnsweredQuestionsCountPort;
import org.flickit.flickitassessmentcore.application.port.out.subject.LoadSubjectByAssessmentKitIdPort;
import org.flickit.flickitassessmentcore.config.FlickitPlatformRestProperties;
import org.flickit.flickitassessmentcore.application.domain.Subject;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

@Component
@RequiredArgsConstructor
public class SubjectRestAdapter implements LoadSubjectByAssessmentKitIdPort, GetSubjectAnsweredQuestionsCountPort {

    private final RestTemplate flickitPlatformRestTemplate;
    private final FlickitPlatformRestProperties properties;
    private final QuestionRestAdapter questionRestAdapter;

    @Override
    public List<Subject> loadByAssessmentKitId(Long assessmentKitId) {
        return loadSubjectsDtoByAssessmentKitId(assessmentKitId).stream()
            .map(SubjectDto::dtoToDomain)
            .toList();
    }

    public List<SubjectDto> loadSubjectsDtoByAssessmentKitId(Long assessmentKitId) {
        String url = String.format(properties.getBaseUrl() + properties.getGetSubjectsUrl(), assessmentKitId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Long>> requestEntity = new HttpEntity<>(null, headers);
        var responseEntity = flickitPlatformRestTemplate.exchange(
            url,
            HttpMethod.GET,
            requestEntity,
            new ParameterizedTypeReference<DataItemsDto<SubjectDto>>() {
            }
        );
        if (!responseEntity.getStatusCode().is2xxSuccessful())
            throw new FlickitPlatformRestException(responseEntity.getStatusCode().value());

        return responseEntity.getBody() != null && responseEntity.getBody().items() != null ?
            responseEntity.getBody().items() :
            List.of();
    }

    @Override
    public Result getSubjectAnsweredQuestionsCount(UUID assessmentId, Long subjectId) {
        var impactfulQuestions = questionRestAdapter.loadImpactfulQuestionsBySubjectId(subjectId);
        return null;
    }
}

