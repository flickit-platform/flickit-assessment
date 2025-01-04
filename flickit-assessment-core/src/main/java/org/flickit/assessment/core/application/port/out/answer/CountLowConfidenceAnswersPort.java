package org.flickit.assessment.core.application.port.out.answer;

import org.flickit.assessment.core.application.domain.ConfidenceLevel;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface CountLowConfidenceAnswersPort {

    /**
     * Counts the number of answers with a confidence level less than the specified level
     * for a given assessment result.
     *
     * @param assessmentResultId the unique identifier of the assessment result
     * @param confidence the confidence level threshold
     * @return the count of answers with confidence less than the specified level
     */
    int countWithConfidenceLessThan(UUID assessmentResultId, ConfidenceLevel confidence);

    /**
     * Counts the number of answers with a confidence level less than the specified level
     * for a given assessment result and a list of questionnaire IDs.
     *
     * @param assessmentResultId the unique identifier of the assessment result
     * @param questionnaireId the list of questionnaire IDs to filter the answers
     * @param confidence the confidence level threshold
     * @return a map where the key is the questionnaire ID and the value is the count of answers
     *         with confidence less than the specified level for that questionnaire
     */
    Map<Long, Integer> countWithConfidenceLessThan(UUID assessmentResultId, List<Long> questionnaireId, ConfidenceLevel confidence);
}
