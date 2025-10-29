package org.flickit.assessment.core.adapter.out.persistence.kit.question;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.error.ErrorMessageKey;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.adapter.out.persistence.kit.answeroption.AnswerOptionMapper;
import org.flickit.assessment.core.adapter.out.persistence.kit.questionnaire.QuestionnaireMapper;
import org.flickit.assessment.core.application.domain.AnswerOption;
import org.flickit.assessment.core.application.domain.Question;
import org.flickit.assessment.core.application.port.out.question.LoadQuestionMayNotBeApplicablePort;
import org.flickit.assessment.core.application.port.out.question.LoadQuestionPort;
import org.flickit.assessment.core.application.port.out.question.LoadQuestionnaireQuestionListPort;
import org.flickit.assessment.core.application.port.out.question.LoadQuestionsBySubjectPort;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaEntity;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaRepository;
import org.flickit.assessment.data.jpa.kit.answeroption.AnswerOptionJpaEntity;
import org.flickit.assessment.data.jpa.kit.answeroption.AnswerOptionJpaRepository;
import org.flickit.assessment.data.jpa.kit.assessmentkit.AssessmentKitJpaRepository;
import org.flickit.assessment.data.jpa.kit.question.QuestionJpaEntity;
import org.flickit.assessment.data.jpa.kit.question.QuestionJpaRepository;
import org.flickit.assessment.data.jpa.kit.questionnaire.QuestionnaireJpaRepository;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;

@Component("coreQuestionPersistenceJpaAdapter")
@RequiredArgsConstructor
public class QuestionPersistenceJpaAdapter implements
    LoadQuestionsBySubjectPort,
    LoadQuestionnaireQuestionListPort,
    LoadQuestionMayNotBeApplicablePort,
    LoadQuestionPort {

    private final QuestionJpaRepository repository;
    private final AnswerOptionJpaRepository answerOptionRepository;
    private final QuestionnaireJpaRepository questionnaireRepository;
    private final AssessmentResultJpaRepository assessmentResultRepository;
    private final AssessmentKitJpaRepository assessmentKitRepository;

    @Override
    public List<Question> loadQuestionsBySubject(long subjectId, long kitVersionId) {
        return repository.findBySubjectId(subjectId, kitVersionId).stream()
            .map(q -> QuestionMapper.mapToDomainModelWithImpacts(q.getId(), null))
            .toList();
    }

    @Override
    public PaginatedResponse<Question> loadByQuestionnaireId(Long questionnaireId, UUID assessmentId, int size, int page) {
        var assessmentResult = assessmentResultRepository.findFirstByAssessment_IdOrderByLastModificationTimeDesc(assessmentId)
            .orElseThrow(() -> new ResourceNotFoundException(COMMON_ASSESSMENT_RESULT_NOT_FOUND));
        var language = resolveLanguage(assessmentResult);

        var pageResult = repository.findAllByQuestionnaireIdAndKitVersionIdOrderByIndex(questionnaireId,
            assessmentResult.getKitVersionId(),
            PageRequest.of(page, size));
        List<Long> answerRangeIds = pageResult.getContent().stream()
            .map(QuestionJpaEntity::getAnswerRangeId)
            .toList();
        var answerRangeIdToAnswerOptionsMap = answerOptionRepository.findAllByAnswerRangeIdInAndKitVersionId(answerRangeIds,
                assessmentResult.getKitVersionId(),
                Sort.by(AnswerOptionJpaEntity.Fields.index)).stream()
            .collect(groupingBy(AnswerOptionJpaEntity::getAnswerRangeId, toList()));

        var items = pageResult.getContent().stream()
            .map(q -> {
                List<AnswerOption> answerOptions = answerRangeIdToAnswerOptionsMap.get(q.getAnswerRangeId()).stream()
                    .map(answerOption -> AnswerOptionMapper.mapToDomainModel(answerOption, language))
                    .toList();
                Question question = QuestionMapper.mapToDomainModel(q, language);
                question.setOptions(answerOptions);
                return question;
            })
            .toList();

        return new PaginatedResponse<>(
            items,
            pageResult.getNumber(),
            pageResult.getSize(),
            QuestionJpaEntity.Fields.index,
            Sort.Direction.ASC.name().toLowerCase(),
            (int) pageResult.getTotalElements()
        );
    }

    @Override
    public boolean loadMayNotBeApplicableById(Long id, long kitVersionId) {
        return repository.findByIdAndKitVersionId(id, kitVersionId)
            .orElseThrow(() -> new ResourceNotFoundException(SUBMIT_ANSWER_QUESTION_ID_NOT_FOUND))
            .getMayNotBeApplicable();
    }

    @Override
    public Question loadByIdAndKitVersionId(Long id, Long kitVersionId) {
        var questionEntity = repository.findByIdAndKitVersionId(id, kitVersionId);
        if (questionEntity.isEmpty())
            throw new ResourceNotFoundException(QUESTION_ID_NOT_FOUND);

        var questionnaire = questionnaireRepository.findByIdAndKitVersionId(questionEntity.get().getQuestionnaireId(), kitVersionId)
            .map(QuestionnaireMapper::mapToDomainModel)
            .orElseThrow(() -> new ResourceNotFoundException(QUESTIONNAIRE_ID_NOT_FOUND));

        return QuestionMapper.mapToDomainWithQuestionnaire(questionEntity.get(), questionnaire);
    }

    @Override
    public int loadFirstUnansweredQuestionIndex(long questionnaireId, UUID assessmentResultId) {
        return repository.findQuestionnaireFirstUnansweredQuestion(questionnaireId, assessmentResultId);
    }

    @Override
    public List<IdAndAnswerRange> loadIdAndAnswerRangeIdByKitVersionId(long kitVersionId) {
        return repository.findIdAndAnswerRangeIdByKitVersionId(kitVersionId).stream()
            .map(entity -> new IdAndAnswerRange(entity.getQuestionId(), entity.getAnswerRangeId()))
            .toList();
    }

    private @Nullable KitLanguage resolveLanguage(AssessmentResultJpaEntity assessmentResult) {
        var assessmentKit = assessmentKitRepository.findByKitVersionId(assessmentResult.getKitVersionId())
            .orElseThrow(() -> new ResourceNotFoundException(ErrorMessageKey.COMMON_ASSESSMENT_KIT_NOT_FOUND));
        return Objects.equals(assessmentResult.getLangId(), assessmentKit.getLanguageId())
            ? null
            : KitLanguage.valueOfById(assessmentResult.getLangId());
    }
}
