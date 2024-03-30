package org.flickit.assessment.kit.adapter.out.persistence.questionnaire;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.data.jpa.core.answer.AnswerJpaRepository;
import org.flickit.assessment.data.jpa.core.answer.QuestionnaireIdAndAnswerCountView;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaEntity;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaRepository;
import org.flickit.assessment.data.jpa.kit.questionnaire.QuestionnaireJpaEntity;
import org.flickit.assessment.data.jpa.kit.questionnaire.QuestionnaireJpaRepository;
import org.flickit.assessment.data.jpa.kit.questionnaire.QuestionnaireListItemView;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaRepository;
import org.flickit.assessment.data.jpa.kit.subject.SubjectWithQuestionnaireIdView;
import org.flickit.assessment.kit.application.domain.Questionnaire;
import org.flickit.assessment.kit.application.domain.QuestionnaireListItem;
import org.flickit.assessment.kit.application.port.out.questionnaire.CreateQuestionnairePort;
import org.flickit.assessment.kit.application.port.out.questionnaire.LoadQuestionnairesByAssessmentIdPort;
import org.flickit.assessment.kit.application.port.out.questionnaire.UpdateQuestionnairePort;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.stream.Collectors;

import static org.flickit.assessment.kit.adapter.out.persistence.questionnaire.QuestionnaireMapper.mapToListItem;
import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_QUESTIONNAIRE_LIST_ASSESSMENT_RESULT_ID_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class QuestionnairePersistenceJpaAdapter implements
    CreateQuestionnairePort,
    UpdateQuestionnairePort,
    LoadQuestionnairesByAssessmentIdPort {

    private final QuestionnaireJpaRepository repository;
    private final AssessmentResultJpaRepository assessmentResultRepository;
    private final SubjectJpaRepository subjectRepository;
    private final AnswerJpaRepository answerRepository;

    @Override
    public Long persist(Questionnaire questionnaire, long kitVersionId, UUID createdBy) {
        return repository.save(QuestionnaireMapper.mapToJpaEntityToPersist(questionnaire, kitVersionId, createdBy)).getId();
    }

    @Override
    public void update(UpdateQuestionnairePort.Param param) {
        repository.update(param.id(),
            param.title(),
            param.index(),
            param.description(),
            param.lastModificationTime(),
            param.lastModifiedBy());
    }

    @Override
    public PaginatedResponse<QuestionnaireListItem> loadAllByAssessmentId(LoadQuestionnairesByAssessmentIdPort.Param param) {
        AssessmentResultJpaEntity assessmentResult = assessmentResultRepository.findFirstByAssessment_IdOrderByLastModificationTimeDesc(param.assessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(GET_QUESTIONNAIRE_LIST_ASSESSMENT_RESULT_ID_NOT_FOUND));

        var pageResult = repository.findAllWithQuestionCountByKitVersionId(assessmentResult.getKitVersionId(), PageRequest.of(param.page(), param.size()));
        var ids = pageResult.getContent().stream().map(QuestionnaireListItemView::getId).toList();

        var questionnaireIdToSubjectMap = subjectRepository.findAllWithQuestionnaireIdByKitVersionId(ids)
            .stream()
            .collect(Collectors.groupingBy(SubjectWithQuestionnaireIdView::getQuestionnaireId));

        var questionnairesProgress = answerRepository.getQuestionnairesProgressByAssessmentResultId(assessmentResult.getId(), ids)
            .stream()
            .collect(Collectors.toMap(QuestionnaireIdAndAnswerCountView::getQuestionnaireId, QuestionnaireIdAndAnswerCountView::getAnswerCount));

        var items = pageResult.getContent().stream()
            .map(q -> mapToListItem(q,
                questionnaireIdToSubjectMap.get(q.getId()),
                questionnairesProgress.getOrDefault(q.getId(), 0)))
            .toList();

        return new PaginatedResponse<>(
            items,
            pageResult.getNumber(),
            pageResult.getSize(),
            QuestionnaireJpaEntity.Fields.INDEX,
            Sort.Direction.ASC.name().toLowerCase(),
            (int) pageResult.getTotalElements()
        );
    }
}
