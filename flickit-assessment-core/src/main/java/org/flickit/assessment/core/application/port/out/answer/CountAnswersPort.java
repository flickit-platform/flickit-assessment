package org.flickit.assessment.core.application.port.out.answer;

import java.util.List;
import java.util.UUID;

public interface CountAnswersPort {

    /**
     * Counts the number of answers associated with the specified questions
     * in a given assessment result.
     *
     * @param assessmentResultId the unique identifier of the assessment result
     * @param questionIds        a list of question IDs for which the answers are counted
     * @return the count of answers for the given question IDs in the assessment result
     */
    int countByQuestionIds(UUID assessmentResultId, List<Long> questionIds);

    /**
     * Counts the number of unapproved answers for a given assessment result.
     *
     * @param assessmentResultId the unique identifier of the assessment result
     * @return the count of unapproved answers in the assessment result
     */
    int countUnapprovedAnswers(UUID assessmentResultId);

    /**
     * Checks if the answer to a given question in a specific assessment result is unapproved.
     *
     * @param assessmentResultId the unique identifier of the assessment result
     * @param questionId         the unique identifier of the question
     * @return {@code true} if the answer is unapproved, otherwise {@code false}
     */
    boolean hasUnapprovedAnswer(UUID assessmentResultId, Long questionId);
}
