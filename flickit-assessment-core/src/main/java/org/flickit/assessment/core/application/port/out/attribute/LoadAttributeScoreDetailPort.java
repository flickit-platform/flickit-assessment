package org.flickit.assessment.core.application.port.out.attribute;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.port.in.attribute.GetAttributeScoreDetailUseCase;

import java.util.UUID;

public interface LoadAttributeScoreDetailPort {

    /**
     * Retrieves the detailed scores for a specific attribute in a given assessment.
     *
     * @param assessmentId    The UUID of the assessment.
     * @param attributeId     The ID of the attribute.
     * @param maturityLevelId The ID of the maturity level.
     * @return A list of {@link GetAttributeScoreDetailUseCase.Questionnaire} items representing the detailed scores.
     * The list is ordered by questionnaireTitle and questionIndex.
     * @throws ResourceNotFoundException if the assessment result is not found.
     */
    PaginatedResponse<Result> loadScoreDetail(
        UUID assessmentId,
        long attributeId,
        long maturityLevelId);

    record Result(String questionnaireTitle,
                  String questionTitle,
                  int index,
                  String answer,
                  Boolean answerIsNotApplicable,
                  int questionWeight,
                  Double answerScore,
                  double weightedScore,
                  int confidence) {
    }
}
