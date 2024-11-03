package org.flickit.assessment.core.adapter.out.persistence.kit.questionnaire;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.QuestionnaireListItem;
import org.flickit.assessment.core.application.port.out.questionnaire.LoadQuestionnairesByAssessmentIdPort;
import org.flickit.assessment.data.jpa.core.answer.AnswerJpaRepository;
import org.flickit.assessment.data.jpa.core.answer.QuestionnaireIdAndAnswerCountView;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaEntity;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaRepository;
import org.flickit.assessment.data.jpa.kit.question.FirstUnansweredQuestionView;
import org.flickit.assessment.data.jpa.kit.question.QuestionJpaRepository;
import org.flickit.assessment.data.jpa.kit.questionnaire.QuestionnaireJpaEntity;
import org.flickit.assessment.data.jpa.kit.questionnaire.QuestionnaireJpaRepository;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaRepository;
import org.flickit.assessment.data.jpa.kit.subject.SubjectWithQuestionnaireIdView;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

import static org.flickit.assessment.core.common.ErrorMessageKey.GET_ASSESSMENT_QUESTIONNAIRE_LIST_ASSESSMENT_RESULT_ID_NOT_FOUND;

@Component(value = "coreQuestionnairePersistenceJpaAdapter")
@RequiredArgsConstructor
public class QuestionnairePersistenceJpaAdapter implements
    LoadQuestionnairesByAssessmentIdPort {

    private final QuestionnaireJpaRepository repository;
    private final AssessmentResultJpaRepository assessmentResultRepository;
    private final SubjectJpaRepository subjectRepository;
    private final AnswerJpaRepository answerRepository;
    private final QuestionJpaRepository questionRepository;

    @Override
    public PaginatedResponse<QuestionnaireListItem> loadAllByAssessmentId(LoadQuestionnairesByAssessmentIdPort.Param param) {
        AssessmentResultJpaEntity assessmentResult = assessmentResultRepository.findFirstByAssessment_IdOrderByLastModificationTimeDesc(param.assessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(GET_ASSESSMENT_QUESTIONNAIRE_LIST_ASSESSMENT_RESULT_ID_NOT_FOUND));

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
                questionnaireToNextQuestionMap.getOrDefault(q.getQuestionnaire().getId(), 1)))
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
}
