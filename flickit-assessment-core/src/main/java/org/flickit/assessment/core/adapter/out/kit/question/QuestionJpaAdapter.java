package org.flickit.assessment.core.adapter.out.kit.question;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.port.in.assessment.GetAttributeScoreDetailUseCase;
import org.flickit.assessment.core.application.port.out.question.LoadAttributeScoreDetailPort;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaRepository;
import org.flickit.assessment.data.jpa.kit.question.QuestionJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.core.common.ErrorMessageKey.GET_ATTRIBUTE_SCORE_DETAIL_ASSESSMENT_RESULT_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class QuestionJpaAdapter implements LoadAttributeScoreDetailPort {

    private final QuestionJpaRepository repository;
    private final AssessmentResultJpaRepository assessmentResultRepository;

    @Override
    public List<GetAttributeScoreDetailUseCase.QuestionScore> load(long attributeId, long maturityLevelId, UUID assessmentId) {
        var assessmentResult = assessmentResultRepository.findFirstByAssessment_IdOrderByLastModificationTimeDesc(assessmentId)
            .orElseThrow(() -> new ResourceNotFoundException(GET_ATTRIBUTE_SCORE_DETAIL_ASSESSMENT_RESULT_NOT_FOUND));

        var questionsView = repository.findImpactFullQuestionsScore(attributeId, maturityLevelId, assessmentResult.getId());

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
