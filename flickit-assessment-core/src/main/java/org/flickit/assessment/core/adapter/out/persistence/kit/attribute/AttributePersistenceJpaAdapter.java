package org.flickit.assessment.core.adapter.out.persistence.kit.attribute;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.port.in.attribute.GetAttributeScoreDetailUseCase;
import org.flickit.assessment.core.application.port.out.attribute.LoadAttributeScoreDetailPort;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaRepository;
import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.core.common.ErrorMessageKey.GET_ATTRIBUTE_SCORE_DETAIL_ASSESSMENT_RESULT_NOT_FOUND;

@Component("coreAttributePersistenceJpaAdapter")
@RequiredArgsConstructor
public class AttributePersistenceJpaAdapter implements LoadAttributeScoreDetailPort {

    private final AttributeJpaRepository repository;
    private final AssessmentResultJpaRepository assessmentResultRepository;

    @Override
    public List<GetAttributeScoreDetailUseCase.QuestionScore> loadScoreDetail(UUID assessmentId, long attributeId, long maturityLevelId) {
        var assessmentResult = assessmentResultRepository.findFirstByAssessment_IdOrderByLastModificationTimeDesc(assessmentId)
            .orElseThrow(() -> new ResourceNotFoundException(GET_ATTRIBUTE_SCORE_DETAIL_ASSESSMENT_RESULT_NOT_FOUND));

        var questionsView = repository.findImpactFullQuestionsScore(assessmentResult.getId(), attributeId, maturityLevelId);

        return questionsView.stream().map(view ->
            new GetAttributeScoreDetailUseCase.QuestionScore(
                view.getQuestionnaireTitle(),
                view.getQuestionIndex(),
                view.getQuestionTitle(),
                view.getQuestionImpact().getWeight(),
                view.getOptionIndex(),
                view.getOptionTitle(),
                view.getOptionImpact() == null ? null : view.getOptionImpact().getValue(),
                view.getOptionImpact() == null ? 0 : view.getOptionImpact().getValue() * view.getQuestionImpact().getWeight()
            )).toList();
    }
}
