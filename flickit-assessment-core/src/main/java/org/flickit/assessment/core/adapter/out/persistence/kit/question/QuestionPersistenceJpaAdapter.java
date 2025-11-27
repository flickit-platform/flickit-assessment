package org.flickit.assessment.core.adapter.out.persistence.kit.question;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.error.ErrorMessageKey;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.adapter.out.persistence.kit.answeroption.AnswerOptionMapper;
import org.flickit.assessment.core.application.domain.AnswerOption;
import org.flickit.assessment.core.application.domain.Question;
import org.flickit.assessment.core.application.port.out.question.LoadQuestionMayNotBeApplicablePort;
import org.flickit.assessment.core.application.port.out.question.LoadQuestionPort;
import org.flickit.assessment.core.application.port.out.question.LoadQuestionnaireQuestionListPort;
import org.flickit.assessment.data.jpa.kit.answeroption.AnswerOptionJpaEntity;
import org.flickit.assessment.data.jpa.kit.answeroption.AnswerOptionJpaRepository;
import org.flickit.assessment.data.jpa.kit.assessmentkit.AssessmentKitJpaRepository;
import org.flickit.assessment.data.jpa.kit.question.QuestionJpaEntity;
import org.flickit.assessment.data.jpa.kit.question.QuestionJpaRepository;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_ASSESSMENT_QUESTION_QUESTION_ID_NOT_FOUND;
import static org.flickit.assessment.core.common.ErrorMessageKey.SUBMIT_ANSWER_QUESTION_ID_NOT_FOUND;

@Component("coreQuestionPersistenceJpaAdapter")
@RequiredArgsConstructor
public class QuestionPersistenceJpaAdapter implements
    LoadQuestionnaireQuestionListPort,
    LoadQuestionMayNotBeApplicablePort,
    LoadQuestionPort {

    private final QuestionJpaRepository repository;
    private final AnswerOptionJpaRepository answerOptionRepository;
    private final AssessmentKitJpaRepository assessmentKitRepository;

    @Override
    public PaginatedResponse<Question> loadByQuestionnaireId(LoadQuestionsParam param) {
        var language = resolveLanguage(param.kitVersionId(), param.langId());

        var pageResult = repository.findAllByQuestionnaireIdAndKitVersionIdOrderByIndex(param.questionnaireId(),
            param.kitVersionId(),
            PageRequest.of(param.page(), param.size()));
        List<Long> answerRangeIds = pageResult.getContent().stream()
            .map(QuestionJpaEntity::getAnswerRangeId)
            .toList();
        var answerRangeIdToAnswerOptionsMap = answerOptionRepository.findAllByAnswerRangeIdInAndKitVersionId(answerRangeIds,
                param.kitVersionId(),
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
    public int loadFirstUnansweredQuestionIndex(long questionnaireId, UUID assessmentResultId) {
        return repository.findQuestionnaireFirstUnansweredQuestion(questionnaireId, assessmentResultId);
    }

    @Override
    public Question loadQuestion(long questionId, long kitVersionId, int langId) {
        var language = resolveLanguage(kitVersionId, langId);

        var questionEntity = repository.findByIdAndKitVersionId(questionId, kitVersionId)
            .orElseThrow(() -> new ResourceNotFoundException(GET_ASSESSMENT_QUESTION_QUESTION_ID_NOT_FOUND));
        var question = QuestionMapper.mapToDomainModel(questionEntity, language);

        var answerOptions = answerOptionRepository.findAllByAnswerRangeIdAndKitVersionIdOrderByIndex(questionEntity.getAnswerRangeId(), kitVersionId)
            .stream().map(e -> AnswerOptionMapper.mapToDomainModel(e, language))
            .toList();
        question.setOptions(answerOptions);
        return question;
    }

    @Override
    public List<IdAndAnswerRange> loadIdAndAnswerRangeIdByKitVersionId(long kitVersionId) {
        return repository.findIdAndAnswerRangeIdByKitVersionId(kitVersionId).stream()
            .map(entity -> new IdAndAnswerRange(entity.getQuestionId(), entity.getAnswerRangeId()))
            .toList();
    }

    private @Nullable KitLanguage resolveLanguage(long kitVersionId, int resultLangId) {
        var assessmentKit = assessmentKitRepository.findByKitVersionId(kitVersionId)
            .orElseThrow(() -> new ResourceNotFoundException(ErrorMessageKey.COMMON_ASSESSMENT_KIT_NOT_FOUND));
        return Objects.equals(resultLangId, assessmentKit.getLanguageId())
            ? null
            : KitLanguage.valueOfById(resultLangId);
    }
}
