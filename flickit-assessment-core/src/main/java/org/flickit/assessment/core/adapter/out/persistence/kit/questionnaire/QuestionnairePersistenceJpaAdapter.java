package org.flickit.assessment.core.adapter.out.persistence.kit.questionnaire;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.core.application.domain.QuestionnaireListItem;
import org.flickit.assessment.core.application.port.out.questionnaire.LoadQuestionnairesByAssessmentIdPort;
import org.flickit.assessment.core.application.port.out.questionnaire.LoadQuestionnairesPort;
import org.flickit.assessment.data.jpa.core.answer.AnswerJpaRepository;
import org.flickit.assessment.data.jpa.core.answer.QuestionnaireIdAndAnswerCountView;
import org.flickit.assessment.data.jpa.kit.question.FirstUnansweredQuestionView;
import org.flickit.assessment.data.jpa.kit.question.QuestionJpaRepository;
import org.flickit.assessment.data.jpa.kit.questionnaire.QuestionnaireJpaEntity;
import org.flickit.assessment.data.jpa.kit.questionnaire.QuestionnaireJpaRepository;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaRepository;
import org.flickit.assessment.data.jpa.kit.subject.SubjectWithQuestionnaireIdView;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component(value = "coreQuestionnairePersistenceJpaAdapter")
@RequiredArgsConstructor
public class QuestionnairePersistenceJpaAdapter implements
    LoadQuestionnairesByAssessmentIdPort,
    LoadQuestionnairesPort {

    private final QuestionnaireJpaRepository repository;
    private final SubjectJpaRepository subjectRepository;
    private final AnswerJpaRepository answerRepository;
    private final QuestionJpaRepository questionRepository;

    @Override
    public PaginatedResponse<QuestionnaireListItem> loadAllByAssessmentId(LoadQuestionnairesByAssessmentIdPort.Param param) {
        var assessmentResult = param.assessmentResult();
        var language = Objects.equals(assessmentResult.getLanguage(), assessmentResult.getAssessment().getAssessmentKit().getLanguage())
            ? null
            : assessmentResult.getLanguage();

        var pageResult = repository.findAllWithQuestionCountByKitVersionId(assessmentResult.getKitVersionId(), PageRequest.of(param.page(), param.size()));
        var ids = pageResult.getContent().stream().map(v -> v.getQuestionnaire().getId()).toList();

        var questionnaireIdToSubjectMap = subjectRepository.findAllWithQuestionnaireIdByKitVersionId(ids, assessmentResult.getKitVersionId())
            .stream()
            .collect(Collectors.groupingBy(SubjectWithQuestionnaireIdView::getQuestionnaireId));

        var questionnairesProgress = answerRepository.getQuestionnairesProgressByAssessmentResultId(assessmentResult.getId(), ids)
            .stream()
            .collect(Collectors.toMap(QuestionnaireIdAndAnswerCountView::getQuestionnaireId, QuestionnaireIdAndAnswerCountView::getAnswerCount));

        var questionnaireToNextQuestionMap = questionRepository.findQuestionnairesFirstUnansweredQuestion(assessmentResult.getId()).stream()
            .collect(Collectors.toMap(FirstUnansweredQuestionView::getQuestionnaireId, FirstUnansweredQuestionView::getIndex));

        var items = pageResult.getContent().stream()
            .map(q -> QuestionnaireMapper.mapToListItem(q,
                questionnaireIdToSubjectMap.get(q.getQuestionnaire().getId()),
                questionnairesProgress.getOrDefault(q.getQuestionnaire().getId(), 0),
                questionnaireToNextQuestionMap.getOrDefault(q.getQuestionnaire().getId(), 1),
                language))
            .toList();

        return new PaginatedResponse<>(
            items,
            pageResult.getNumber(),
            pageResult.getSize(),
            QuestionnaireJpaEntity.Fields.index,
            Sort.Direction.ASC.name().toLowerCase(),
            (int) pageResult.getTotalElements()
        );
    }

    @Override
    public Map<Long, Result> loadQuestionnaireDetails(long kitVersionId, UUID assessmentResultId) {
        var questionnaireViews = repository.findAllWithQuestionCountByKitVersionId(kitVersionId, null);
        var questionnaireIds = questionnaireViews.getContent().stream()
            .map(v -> v.getQuestionnaire().getId())
            .toList();

        var questionnaireIdToAnswerCountMap = answerRepository.getQuestionnairesProgressByAssessmentResultId(assessmentResultId, questionnaireIds)
            .stream()
            .collect(Collectors.toMap(QuestionnaireIdAndAnswerCountView::getQuestionnaireId, QuestionnaireIdAndAnswerCountView::getAnswerCount));

        return questionnaireViews.stream()
            .map(view -> {
                var questionnaire = view.getQuestionnaire();
                int answerCount = questionnaireIdToAnswerCountMap.getOrDefault(questionnaire.getId(), 0);
                return new LoadQuestionnairesPort.Result(
                    questionnaire.getId(),
                    questionnaire.getIndex(),
                    questionnaire.getTitle(),
                    view.getQuestionCount(),
                    answerCount
                );
            })
            .collect(Collectors.toMap(Result::id, Function.identity()));
    }
}
