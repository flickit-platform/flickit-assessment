package org.flickit.assessment.core.adapter.out.persistence.kit.attribute;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.Order;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.application.domain.kitcustom.KitCustomData;
import org.flickit.assessment.common.error.ErrorMessageKey;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.common.util.JsonUtils;
import org.flickit.assessment.core.adapter.out.persistence.answer.AnswerMapper;
import org.flickit.assessment.core.adapter.out.persistence.kit.answeroption.AnswerOptionMapper;
import org.flickit.assessment.core.adapter.out.persistence.kit.question.QuestionMapper;
import org.flickit.assessment.core.adapter.out.persistence.kit.questionimpact.QuestionImpactMapper;
import org.flickit.assessment.core.application.domain.Attribute;
import org.flickit.assessment.core.application.port.in.attribute.GetAttributeScoreDetailUseCase;
import org.flickit.assessment.core.application.port.out.attribute.*;
import org.flickit.assessment.data.jpa.core.answer.AnswerJpaEntity;
import org.flickit.assessment.data.jpa.core.assessment.AssessmentJpaRepository;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaEntity;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaRepository;
import org.flickit.assessment.data.jpa.core.attribute.AttributeMaturityLevelSubjectView;
import org.flickit.assessment.data.jpa.kit.assessmentkit.AssessmentKitJpaRepository;
import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaRepository;
import org.flickit.assessment.data.jpa.kit.kitcustom.KitCustomJpaRepository;
import org.flickit.assessment.data.jpa.kit.questionimpact.QuestionImpactJpaEntity;
import org.flickit.assessment.data.jpa.kit.questionnaire.QuestionnaireJpaEntity;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.*;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.core.adapter.out.persistence.attributematurityscore.AttributeMaturityScoreMapper.mapToAttributeScoreDetail;
import static org.flickit.assessment.core.adapter.out.persistence.kit.attribute.AttributeMapper.mapToDomainModel;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;

@Component("coreAttributePersistenceJpaAdapter")
@RequiredArgsConstructor
public class AttributePersistenceJpaAdapter implements
    LoadAttributeScoreDetailPort,
    LoadAttributePort,
    LoadAttributeScoresPort,
    CountAttributesPort,
    LoadAttributesPort,
    LoadAttributeQuestionsPort {

    private final AttributeJpaRepository repository;
    private final AssessmentResultJpaRepository assessmentResultRepository;
    private final KitCustomJpaRepository kitCustomRepository;
    private final AssessmentJpaRepository assessmentRepository;
    private final AssessmentKitJpaRepository assessmentKitRepository;

    @Override
    public PaginatedResponse<LoadAttributeScoreDetailPort.Result> loadScoreDetail(LoadAttributeScoreDetailPort.Param param) {
        var assessmentResult = assessmentResultRepository.findFirstByAssessment_IdOrderByLastModificationTimeDesc(param.assessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(GET_ATTRIBUTE_SCORE_DETAIL_ASSESSMENT_RESULT_NOT_FOUND));
        var translationLanguage = resolveLanguage(assessmentResult);

        var pageRequest = buildPageRequest(param.page(), param.size(), param.sort(), param.order());
        var pageResult = repository.findImpactFullQuestionsScore(
            assessmentResult.getAssessment().getId(),
            assessmentResult.getId(),
            assessmentResult.getKitVersionId(),
            param.attributeId(),
            param.maturityLevelId(),
            pageRequest);

        var items = pageResult.getContent().stream()
            .map(view -> mapToAttributeScoreDetail(view, translationLanguage))
            .toList();

        return new PaginatedResponse<>(
            items,
            pageRequest.getPageNumber(),
            pageRequest.getPageSize(),
            param.sort().getTitle(),
            param.order().getTitle(),
            (int) pageResult.getTotalElements()
        );
    }

    private PageRequest buildPageRequest(int page, int size, GetAttributeScoreDetailUseCase.Param.Sort sort, Order order) {
        var orderDir = switch (order) {
            case Order.ASC -> Sort.Direction.ASC;
            case Order.DESC -> Sort.Direction.DESC;
        };

        String sortField = switch (sort) {
            case GetAttributeScoreDetailUseCase.Param.Sort.QUESTIONNAIRE -> "qr." + QuestionnaireJpaEntity.Fields.title;
            case GetAttributeScoreDetailUseCase.Param.Sort.WEIGHT -> "qi." + QuestionImpactJpaEntity.Fields.weight;
            case GetAttributeScoreDetailUseCase.Param.Sort.CONFIDENCE ->
                "ans." + AnswerJpaEntity.Fields.confidenceLevelId;
            case GetAttributeScoreDetailUseCase.Param.Sort.GAINED_SCORE -> "gainedScore";
            case GetAttributeScoreDetailUseCase.Param.Sort.MISSED_SCORE -> "missedScore";
            case GetAttributeScoreDetailUseCase.Param.Sort.EVIDENCE_COUNT -> "evidenceCount";
        };

        return PageRequest.of(page, size, orderDir, sortField);
    }

    @Override
    public List<LoadAttributeScoresPort.Result> loadScores(UUID assessmentId, long attributeId, long maturityLevelId) {
        var assessmentResult = assessmentResultRepository.findFirstByAssessment_IdOrderByLastModificationTimeDesc(assessmentId)
            .orElseThrow(() -> new ResourceNotFoundException(GET_ATTRIBUTE_SCORE_STATS_ASSESSMENT_RESULT_NOT_FOUND));

        return repository.findScoreStats(assessmentResult.getId(), assessmentResult.getKitVersionId(), attributeId, maturityLevelId)
            .stream()
            .map(view -> new LoadAttributeScoresPort.Result(view.getQuestionId(),
                view.getQuestionWeight(),
                getScore(view.getAnswer(), view.getOptionValue()),
                view.getAnswer() != null && view.getAnswerIsNotApplicable() != null && view.getAnswer().getIsNotApplicable()))
            .toList();
    }

    private Double getScore(AnswerJpaEntity answer, Double optionValue) {
        if (answer == null) // if no answer is submitted for the question
            return 0.0;
        if (Boolean.TRUE.equals(answer.getIsNotApplicable())) // if there is an answer and notApplicable == true
            return null;
        return optionValue;
    }

    @Override
    public Attribute load(Long attributeId, Long kitVersionId) {
        var attribute = repository.findByIdAndKitVersionId(attributeId, kitVersionId)
            .orElseThrow(() -> new ResourceNotFoundException(ATTRIBUTE_ID_NOT_FOUND));
        return mapToDomainModel(attribute);
    }

    @Override
    public int countAttributes(long kitVersionId) {
        return repository.countByKitVersionId(kitVersionId);
    }

    @Override
    public List<LoadAttributesPort.Result> loadAll(UUID assessmentId) {
        var assessment = assessmentRepository.findByIdAndDeletedFalse(assessmentId)
            .orElseThrow(() -> new ResourceNotFoundException(ASSESSMENT_ID_NOT_FOUND));

        var attributeViews = repository.findAllByAssessmentIdWithSubjectAndMaturityLevel(assessmentId);
        var attributes = attributeViews.stream()
            .map(e -> AttributeMapper.mapToDomainModel(e.getAttribute()))
            .toList();

        var attributeIdToWeight = getAttributeIdToWeightMap(attributes,
            assessment.getAssessmentKitId(),
            assessment.getKitCustomId());

        return attributeViews.stream()
            .sorted(Comparator.comparingInt((AttributeMaturityLevelSubjectView v) -> v.getSubject().getIndex())
                .thenComparingInt(v -> v.getAttribute().getIndex()))
            .map(e -> AttributeMapper.mapToResult(e, attributeIdToWeight.get(e.getAttribute().getId())))
            .toList();
    }

    private Map<Long, Integer> getAttributeIdToWeightMap(List<Attribute> attributes, long kitId, Long kitCustomId) {
        if (kitCustomId == null)
            return attributes.stream()
                .collect(toMap(Attribute::getId, Attribute::getWeight));

        var kitCustomEntity = kitCustomRepository.findByIdAndKitId(kitCustomId, kitId)
            .orElseThrow(() -> new ResourceNotFoundException(KIT_CUSTOM_ID_NOT_FOUND));

        var kitCustomData = JsonUtils.fromJson(kitCustomEntity.getCustomData(), KitCustomData.class);

        if (kitCustomData == null || kitCustomData.attributes() == null)
            return attributes.stream()
                .collect(toMap(Attribute::getId, Attribute::getWeight));

        Map<Long, Integer> attributeIdToCustomWeight = kitCustomData.attributes().stream()
            .collect(toMap(KitCustomData.Attribute::id, KitCustomData.Attribute::weight));

        return attributes.stream()
            .collect(toMap(
                Attribute::getId,
                e -> attributeIdToCustomWeight.getOrDefault(e.getId(), e.getWeight())));
    }

    @Override
    public List<LoadAttributeQuestionsPort.Result> loadApplicableQuestions(UUID assessmentId,
                                                                           long attributeId) {
        var assessmentResult = assessmentResultRepository.findFirstByAssessment_IdOrderByLastModificationTimeDesc(assessmentId)
            .orElseThrow(() -> new ResourceNotFoundException(COMMON_ASSESSMENT_RESULT_NOT_FOUND));

        var questionIdToViewMap = repository.findAttributeQuestionsAndAnswers(assessmentResult.getId(),
                assessmentResult.getKitVersionId(),
                attributeId).stream()
            .collect(groupingBy(v -> v.getQuestion().getId()));

        return questionIdToViewMap.values().stream()
            .map(views -> {
                var impacts = views.stream()
                    .map(i -> QuestionImpactMapper.mapToDomainModel(i.getQuestionImpact()))
                    .toList();

                var firstView = views.getFirst();
                var question = QuestionMapper.mapToDomainModelWithImpacts(firstView.getQuestion(), impacts);
                var answerOption = firstView.getAnswerOption() != null
                    ? AnswerOptionMapper.mapToDomainModel(firstView.getAnswerOption())
                    : null;
                var answer = firstView.getAnswer() != null
                    ? AnswerMapper.mapToDomainModel(firstView.getAnswer(), answerOption)
                    : null;
                return new LoadAttributeQuestionsPort.Result(question, answer);
            })
            .toList();
    }

    private KitLanguage resolveLanguage(AssessmentResultJpaEntity assessmentResult) {
        var assessmentKit = assessmentKitRepository.findByKitVersionId(assessmentResult.getKitVersionId())
            .orElseThrow(() -> new ResourceNotFoundException(ErrorMessageKey.COMMON_ASSESSMENT_KIT_NOT_FOUND));
        return Objects.equals(assessmentResult.getLangId(), assessmentKit.getLanguageId())
            ? null
            : KitLanguage.valueOfById(assessmentResult.getLangId());
    }
}
