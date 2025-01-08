package org.flickit.assessment.core.adapter.out.persistence.kit.attribute;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.Order;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.Attribute;
import org.flickit.assessment.core.application.port.in.attribute.GetAttributeScoreDetailUseCase;
import org.flickit.assessment.core.application.port.out.attribute.CountAttributesPort;
import org.flickit.assessment.core.application.port.out.attribute.LoadAttributePort;
import org.flickit.assessment.core.application.port.out.attribute.LoadAttributeScoreDetailPort;
import org.flickit.assessment.core.application.port.out.attribute.LoadAttributeScoresPort;
import org.flickit.assessment.data.jpa.core.answer.AnswerJpaEntity;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaRepository;
import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaRepository;
import org.flickit.assessment.data.jpa.kit.questionimpact.QuestionImpactJpaEntity;
import org.flickit.assessment.data.jpa.kit.questionnaire.QuestionnaireJpaEntity;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.core.adapter.out.persistence.kit.attribute.AttributeMapper.mapToDomainModel;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;

@Component("coreAttributePersistenceJpaAdapter")
@RequiredArgsConstructor
public class AttributePersistenceJpaAdapter implements
    LoadAttributeScoreDetailPort,
    LoadAttributePort,
    LoadAttributeScoresPort,
    CountAttributesPort {

    private final AttributeJpaRepository repository;
    private final AssessmentResultJpaRepository assessmentResultRepository;

    @Override
    public PaginatedResponse<LoadAttributeScoreDetailPort.Result> loadScoreDetail(LoadAttributeScoreDetailPort.Param param) {
        var assessmentResult = assessmentResultRepository.findFirstByAssessment_IdOrderByLastModificationTimeDesc(param.assessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(GET_ATTRIBUTE_SCORE_DETAIL_ASSESSMENT_RESULT_NOT_FOUND));

        var pageRequest = buildPageRequest(param.page(), param.size(), param.sort(), param.order());
        var pageResult = repository.findImpactFullQuestionsScore(
            assessmentResult.getAssessment().getId(),
            assessmentResult.getId(),
            assessmentResult.getKitVersionId(),
            param.attributeId(),
            param.maturityLevelId(),
            pageRequest);

        var items = pageResult.getContent().stream()
            .map(view -> new LoadAttributeScoreDetailPort.Result(view.getQuestionnaireId(),
                view.getQuestionnaireTitle(),
                view.getQuestionId(),
                view.getQuestionIndex(),
                view.getQuestionTitle(),
                view.getQuestionImpact().getWeight(),
                view.getOptionIndex(),
                view.getOptionTitle(),
                view.getAnswer() == null ? null : view.getAnswer().getIsNotApplicable(),
                view.getAnswerScore(),
                view.getWeightedScore(),
                view.getAnswer() != null && view.getAnswer().getConfidenceLevelId() != null ? view.getAnswer().getConfidenceLevelId() : null,
                view.getEvidenceCount()))
            .toList();

        return new PaginatedResponse<>(
            items,
            pageRequest.getPageNumber(),
            pageRequest.getPageSize(),
            param.order().getTitle(),
            param.sort().getTitle(),
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
            case GetAttributeScoreDetailUseCase.Param.Sort.WEIGHTED_SCORE -> "weightedScore";
            case GetAttributeScoreDetailUseCase.Param.Sort.SCORE -> "answerScore";
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
}
